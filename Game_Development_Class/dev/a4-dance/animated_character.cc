#include "animated_character.h"
#include "amc_util.h"

#include <cstdlib>
#include <fstream>
#include <iostream>
#include <sstream>


AnimatedCharacter::AnimatedCharacter(const std::string &asf_filename) :
    fps_(120), elapsed_since_last_frame_(0.0f), current_frame_(0)
{
    LoadSkeleton(asf_filename);
}

AnimatedCharacter::AnimatedCharacter() :
    fps_(120), elapsed_since_last_frame_(0.0f), current_frame_(0)
{
}

AnimatedCharacter::~AnimatedCharacter() {
}


void AnimatedCharacter::LoadSkeleton(const std::string &asf_filename) {
    skeleton_.LoadFromASF(asf_filename);
}


void AnimatedCharacter::Play(const MotionClip &motion_clip) {
    motion_queue_.clear();
    motion_queue_.push_back(motion_clip);
    current_frame_ = 0;
}


void AnimatedCharacter::Queue(const MotionClip &motion_clip) {
    if (motion_queue_.size() == 0) {
        Play(motion_clip);
    }
    else {
        motion_queue_.push_back(motion_clip);
    }
}


void AnimatedCharacter::ClearQueue() {
    motion_queue_.clear();
}


void AnimatedCharacter::OverlayClip(const MotionClip &clip, int num_transition_frames) {
    overlay_clip_ = clip;
    overlay_transition_frames_ = num_transition_frames;
    overlay_frame_ = 0;
}


void AnimatedCharacter::AdvanceAnimation(double dt) {
    if (motion_queue_.size() == 0) {
        pose_ = Pose();
    }
    else {
        elapsed_since_last_frame_ += dt;
        
        double frames_to_advance = fps_ * elapsed_since_last_frame_;
        double whole_frames;
        double frac = modf(frames_to_advance, &whole_frames);
        int nframes = (int)whole_frames;
        elapsed_since_last_frame_ = frac / fps_;
        
        for (int i=0; i<nframes; i++) {
            // advance the main motion track
            current_frame_++;
            // handle end case
            if (current_frame_ >= motion_queue_[0].size()) {
                // loop back to the first frame
                current_frame_ = 0;
                // if there are more motions in the queue then pop this one and goto the next
                if (motion_queue_.size() > 1) {
                    motion_queue_.erase(motion_queue_.begin());
                }
            }
            
            // advance the overlay clip if there is one
            if (overlay_clip_.size()) {
                overlay_frame_++;
                // handle end case
                if (overlay_frame_ >= overlay_clip_.size()) {
                    // done playing overlay, reset frame counter and clear the overlay clip
                    overlay_frame_ = 0;
                    overlay_clip_ = MotionClip();
                }
            }
            
            // update the pose based on new frames
            CalcCurrentPose();

            // add to the translation matrix for the case when relative root motion is used
            accum_translation_matrix_ = accum_translation_matrix_ * pose_.root_relative_translation();
        }
    }
}


void AnimatedCharacter::CalcCurrentPose() {
    if (!overlay_clip_.size()) {
        // no overaly track, motion is entirely from the base track (i.e., the motion queue)
        pose_ = motion_queue_[0][current_frame_];
    }
    else {
        // there is an active overlay track
        if (overlay_frame_ < overlay_transition_frames_) {
            // fade in the overlay
            float alpha = (float)overlay_frame_/(float)overlay_transition_frames_;
            pose_ = motion_queue_[0][current_frame_].Lerp(overlay_clip_[overlay_frame_], alpha);
        }
        else if (overlay_frame_ > overlay_clip_.size() - overlay_transition_frames_) {
            // fade out the overlay
            float alpha = (float)(overlay_clip_.size() - overlay_frame_)/(float)overlay_transition_frames_;
            pose_ = motion_queue_[0][current_frame_].Lerp(overlay_clip_[overlay_frame_], alpha);
        }
        else {
            // overlay is completely faded in, we don't see the base track at all
            pose_ = overlay_clip_[overlay_frame_];
        }
    }
}


Skeleton* AnimatedCharacter::skeleton_ptr() {
    return &skeleton_;
}


void AnimatedCharacter::set_fps(int fps) {
    fps_ = fps;
}


int AnimatedCharacter::fps() {
    return fps_;
}



void AnimatedCharacter::Draw(const Matrix4 &model_matrix, const Matrix4 &view_matrix, const Matrix4 &proj_matrix,
                             bool use_absolute_position)
{
    Matrix4 character_root_transform;
    if (use_absolute_position) {
        // set root position based on the absolute position in the mocap data
        character_root_transform = model_matrix * pose_.RootTransform();
    }
    else {
        // set root position based on the relative updates accumulated each frame
        character_root_transform = model_matrix * accum_translation_matrix_ * pose_.root_rotation();
    }
    
    for (int i=0; i<skeleton_.num_root_bones(); i++) {
        DrawBoneRecursive(skeleton_.root_bone(i), character_root_transform, view_matrix, proj_matrix);
    }
}


void AnimatedCharacter::DrawBoneRecursive(const std::string &bone_name, const Matrix4 &parent_transform,
                                          const Matrix4 &view_matrix, const Matrix4 &proj_matrix)
{
    // Step 1:  Draw this bone
    
    /** TODO: You will need to define a current transformation matrix for this bone that takes into account not just the parent_transform but also the local rotation of the bone due to the current pose.
     
        Think of the vertices that make up the geometry of each bone as being defined in "bone space", where the joint that the bone rotates around is located at the origin and the bone extends in the direction and length specified by the skeleton. (See Skeleton::BoneDirectionAndLength()).
     
        To determine which matrices need to be composed to create the current transformation matrix and the order to multiply them together, think about what needs to happen to each vertex of a cylinder defined in "bone space" in order to get the vertex to the correct position in 3D space.
     
        First, the vertex must be transformed into the bone's "rotation axis space" because the rotation axes are not guaranteed to line up perfectly with the bone's X,Y,Z axes.  The bone's rotation axes are a property of the skeleton -- they are set for each skeleton and do not change for each pose.  You can access a matrix that transforms from "bone space" to "rotation axis space" from the skeleton_ member variable.
     
        Second, now that the vertices are in the bone's "rotation axis space", the rotation from the character's current pose can be applied.  The current pose is stored in the pose_ member variable.

        Third, with the rotations applied relative to the appropriate rotation axes, the vertices must now be transformed back into regular "bone space".  At this point, the bone should be properly rotated based upon the current pose, but the vertices are still defined in "bone space" so they are close to the origin.
     
        Finally, the vertices need to be tranformed to the bone's parent space.
     
        To start, we give you a current transformation matrix (ctm) that only takes this last step into account.
    */
    Matrix4 BoneToRot = skeleton_.BoneSpaceToRotAxesSpace(bone_name);
    Matrix4 curPose = pose_.JointRotation(bone_name);
    Matrix4 RotToBone = skeleton_.RotAxesSpaceToBoneSpace(bone_name);
    Matrix4 ctm = parent_transform*RotToBone*curPose*BoneToRot;


    
    // Here is a good way to check your work -- draw the coordinate axes for each
    // bone.  To start, this will just draw the axes for the root node of the
    // character, but once you add the recursive call to draw the children, this
    // will draw the axes for each bone.
    Matrix4 S = Matrix4::Scale(Vector3(0.15f, 0.15f, 0.15f));
    //quick_shapes_.DrawAxes(ctm*S, view_matrix, proj_matrix);

    
    // TODO: Eventually, you'll want to draw something different depending on which part
    // of the body is being drawn.  An if statement like this is an easy way to do that.
    if (bone_name == "lhipjoint" || bone_name == "rhipjoint") {
        Matrix4 T = Matrix4::Translation(skeleton_.BoneDirectionAndLength(bone_name));
        Point3 a(0,0,0);
        Point3 b = T*a;
        quick_shapes_.DrawLineSegment(ctm, view_matrix, proj_matrix, Color(1.0, .65, 0), a, b, .025f);
    }
    if (bone_name == "lfemur" || bone_name == "rfemur") {
        Matrix4 T = Matrix4::Translation(skeleton_.BoneDirectionAndLength(bone_name));
        Point3 a(0,0,0);
        Point3 b = T*a;
        quick_shapes_.DrawLineSegment(ctm, view_matrix, proj_matrix, Color(1.0, .65, 0), a, b, .025f);
    }
    if (bone_name == "ltibia" || bone_name == "rtibia") {
        Matrix4 T = Matrix4::Translation(skeleton_.BoneDirectionAndLength(bone_name));
        Point3 a(0,0,0);
        Point3 b = T*a;
        quick_shapes_.DrawLineSegment(ctm, view_matrix, proj_matrix, Color(1.0, .65, 0), a, b, .025f);
    }
    if (bone_name == "lfoot" || bone_name == "rfoot") {
        Matrix4 T = Matrix4::Translation(skeleton_.BoneDirectionAndLength(bone_name));
        Point3 a(0,0,0);
        Point3 b = T*a;
        quick_shapes_.DrawLineSegment(ctm, view_matrix, proj_matrix, Color(1.0, .65, 0), a, b, .015f);
    }
    if (bone_name == "ltoes" || bone_name == "rtoes") {
        Matrix4 T = Matrix4::Translation(skeleton_.BoneDirectionAndLength(bone_name));
        Point3 a(0,0,0);
        Point3 b = T*a;
        quick_shapes_.DrawLineSegment(ctm, view_matrix, proj_matrix, Color(1.0, .65, 0), a, b, .015f);
    }
    if (bone_name == "lowerback") {
        Matrix4 T = Matrix4::Translation(skeleton_.BoneDirectionAndLength(bone_name));
        Point3 a(0,0,0);
        Point3 b = T*a;
        T = Matrix4::Translation(Vector3(0, b.y()/2, 0));
        Matrix4 Sn = Matrix4::Scale(Vector3(.05, b.y(), .05));
        quick_shapes_.DrawCube(ctm*T*Sn, view_matrix, proj_matrix, Color(1.0, .65, 0));
    }
    if (bone_name == "upperback") {
        Matrix4 T = Matrix4::Translation(skeleton_.BoneDirectionAndLength(bone_name));
        Point3 a(0,0,0);
        Point3 b = T*a;
        T = Matrix4::Translation(Vector3(0, b.y()/2, 0));
        Matrix4 Sn = Matrix4::Scale(Vector3(.1, b.y(), .05));
        quick_shapes_.DrawCube(ctm*T*Sn, view_matrix, proj_matrix, Color(1.0, .65, 0));
    }
    if (bone_name == "thorax") {
        Matrix4 T = Matrix4::Translation(skeleton_.BoneDirectionAndLength(bone_name));
        Point3 a(0,0,0);
        Point3 b = T*a;
        T = Matrix4::Translation(Vector3(0, b.y()/2, 0));
        Matrix4 Sn = Matrix4::Scale(Vector3(.20, b.y(), .05));
        quick_shapes_.DrawCube(ctm*T*Sn, view_matrix, proj_matrix, Color(1.0, .65, 0));
    }
    if (bone_name == "lowerneck" || bone_name == "upperneck") {
        Matrix4 T = Matrix4::Translation(skeleton_.BoneDirectionAndLength(bone_name));
        Point3 a(0,0,0);
        Point3 b = T*a;
        quick_shapes_.DrawLineSegment(ctm, view_matrix, proj_matrix, Color(1.0, .65, 0), a, b, .025f);
    }
    if (bone_name == "head") {
        Matrix4 Sn = Matrix4::Scale(Vector3(.1, .1, .1));
        quick_shapes_.DrawSphere(ctm*Sn, view_matrix, proj_matrix, Color(1.0, .65, 0));
        Sn = Matrix4::Scale(Vector3(.05, .05, .05));
        Matrix4 T = Matrix4::Translation(Vector3(-0.05, 0.075, 0.05));
        quick_shapes_.DrawSphere(ctm*T*Sn, view_matrix, proj_matrix, Color(1.0, 1.0, 1.0));
        T = Matrix4::Translation(Vector3(0.05, 0.075, 0.05));
        quick_shapes_.DrawSphere(ctm*T*Sn, view_matrix, proj_matrix, Color(1.0, 1.0, 1.0));
    }
    if (bone_name == "lclavicle" || bone_name == "rclavicle") {
        Matrix4 T = Matrix4::Translation(skeleton_.BoneDirectionAndLength(bone_name));
        Point3 a(0,0,0);
        Point3 b = T*a;
        quick_shapes_.DrawLineSegment(ctm, view_matrix, proj_matrix, Color(1.0, .65, 0), a, b, .025f);
    }
    if (bone_name == "lhumerus" || bone_name == "rhumerus" || bone_name == "lradius" || bone_name == "rradius") {
        Matrix4 T = Matrix4::Translation(skeleton_.BoneDirectionAndLength(bone_name));
        Point3 a(0,0,0);
        Point3 b = T*a;
        quick_shapes_.DrawLineSegment(ctm, view_matrix, proj_matrix, Color(1.0, .65, 0), a, b, .025f);
    }
    if (bone_name == "lwrist" || bone_name == "rwrist") {
        Matrix4 T = Matrix4::Translation(skeleton_.BoneDirectionAndLength(bone_name));
        Point3 a(0,0,0);
        Point3 b = T*a;
        quick_shapes_.DrawLineSegment(ctm, view_matrix, proj_matrix, Color(1.0, .65, 0), a, b, .025f);
    }
    if (bone_name == "lhand" || bone_name == "rhand" || bone_name == "lthumb" || bone_name == "rthumb" || bone_name == "rfingers" || bone_name == "lfingers") {
        Matrix4 T = Matrix4::Translation(skeleton_.BoneDirectionAndLength(bone_name));
        Point3 a(0,0,0);
        Point3 b = T*a;
        quick_shapes_.DrawLineSegment(ctm, view_matrix, proj_matrix, Color(1.0, .65, 0), a, b, .025f);
    }
    
    
    // Step 2: Draw the bone's children
    /**
     
    // TODO: Determining the proper child_root_transform is the key here.  It depends on the
    // current transformation matrix, but you also need to take into account the
    // direction and length of the bone in order to reach the root of the children.
    Matrix4 child_root_transform = ??;
     
    for (int i=0; i<skeleton_.num_children(bone_name); i++) {
        DrawBoneRecursive(skeleton_.child_bone(bone_name, i), child_root_transform, view_matrix, proj_matrix);
    }
    **/
   Matrix4 child_root_transform = ctm*skeleton_.BoneSpaceToChildrenSpace(bone_name);
   for (int i=0; i<skeleton_.num_children(bone_name); i++) {
        DrawBoneRecursive(skeleton_.child_bone(bone_name, i), child_root_transform, view_matrix, proj_matrix);
    }
}



