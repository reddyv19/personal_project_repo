/** CSci-4611 Assignment 3:  Earthquake
 */

#include "earth.h"
#include "config.h"

#include <vector>

// for M_PI constant
#define _USE_MATH_DEFINES
#include <math.h>
#include <iostream>

Earth::Earth() {
}

Earth::~Earth() {
}

void Earth::Init(const std::vector<std::string> &search_path) {
    // init shader program
    shader_.Init();
    
    // init texture: you can change to a lower-res texture here if needed
    earth_tex_.InitFromFile(Platform::FindFile("earth-2k.png", search_path));

    // init geometry
    const int nslices = 10;
    const int nstacks = 10;

    // TODO: This is where you need to set the vertices and indiceds for earth_mesh_.

    // As a demo, we'll add a square with 2 triangles.
    std::vector<unsigned int> indices;
    std::vector<Point3> vertices;
    std::vector<Vector3> norms;

    // four vertices
    for(float i = -M_PI/2 ; i <M_PI/2; i += M_PI/10.0){
        for(float j = -M_PI ; j <=M_PI; j += M_PI/5.0){
            vertices.push_back(Point3(j, i, 0));
            vertices.push_back(Point3(j, i+M_PI/10.0, 0));
            norms.push_back(Vector3(0, 0, 1));
            norms.push_back(Vector3(0, 0, 1));
            tex_coords.push_back(Point2((j+M_PI)/(2*M_PI), 1-(i+M_PI_2)/(M_PI)));
            tex_coords.push_back(Point2((j+M_PI)/(2*M_PI), 1-(i+M_PI_2+M_PI/10)/(M_PI)));


        }
    }
    for(float i = 0; i<10; i+=1){
        for(float j = 0; j<10; j+=1){
            indices.push_back(j*2+i*22);
            indices.push_back((j+1)*2+i*22);
            indices.push_back((j+1)*2+i*22+1);

            indices.push_back(j*2+i*22);
            indices.push_back((j+1)*2+i*22+1);
            indices.push_back(j*2+i*22+1);

        }
    }
    earth_mesh_.SetVertices(vertices);
    earth_mesh_.SetIndices(indices);
    earth_mesh_.SetTexCoords(0, tex_coords);
    earth_mesh_.SetNormals(norms);
    earth_mesh_.UpdateGPUMemory();
}

void Earth::SwitchToPlane(){
    std::vector<Point3> vertices;
    std::vector<Vector3> norms;
    for(float i = -M_PI/2 ; i <M_PI/2; i += M_PI/10.0){
        for(float j = -M_PI ; j <=M_PI; j += M_PI/5.0){
            vertices.push_back(Point3(j, i, 0));
            vertices.push_back(Point3(j, i+M_PI/10.0, 0));
            norms.push_back(Vector3(cos(j)*sin(i), sin(j), cos(j)*cos(i)));
            norms.push_back(Vector3(cos(j)*sin(i+M_PI/10.0), sin(j), cos(j)*cos(i+M_PI/10.0)));
        }
    }   
    earth_mesh_.SetVertices(vertices);
    earth_mesh_.SetNormals(norms);
    earth_mesh_.UpdateGPUMemory();

}

void Earth::SwitchToGlobe(){
    std::vector<Point3> vertices;
    std::vector<Vector3> norms;
    for(float i = -M_PI/2 ; i <M_PI/2; i += M_PI/10.0){
        for(float j = -M_PI ; j <=M_PI; j += M_PI/5.0){
            vertices.push_back(Point3(cos(j)*sin(i), sin(j), cos(j)*cos(i)));
            vertices.push_back(Point3(cos(j)*sin(i+M_PI/10.0), sin(j), cos(j)*cos(i+M_PI/10.0)));
            norms.push_back(Vector3(cos(j)*sin(i), sin(j), cos(j)*cos(i)));
            norms.push_back(Vector3(cos(j)*sin(i+M_PI/10.0), sin(j), cos(j)*cos(i+M_PI/10.0)));
        }
    }  
    
     
    earth_mesh_.SetVertices(vertices);
    earth_mesh_.SetNormals(norms);
    earth_mesh_.UpdateGPUMemory();

}



void Earth::Draw(const Matrix4 &model_matrix, const Matrix4 &view_matrix, const Matrix4 &proj_matrix) {
    // Define a really bright white light.  Lighting is a property of the "shader"
    DefaultShader::LightProperties light;
    light.position = Point3(10,10,10);
    light.ambient_intensity = Color(1,1,1);
    light.diffuse_intensity = Color(1,1,1);
    light.specular_intensity = Color(1,1,1);
    shader_.SetLight(0, light);

    // Adust the material properties, material is a property of the thing
    // (e.g., a mesh) that we draw with the shader.  The reflectance properties
    // affect the lighting.  The surface texture is the key for getting the
    // image of the earth to show up.
    DefaultShader::MaterialProperties mat;
    mat.ambient_reflectance = Color(0.5, 0.5, 0.5);
    mat.diffuse_reflectance = Color(0.75, 0.75, 0.75);
    mat.specular_reflectance = Color(0.75, 0.75, 0.75);
    mat.surface_texture = earth_tex_;

    // Draw the earth mesh using these settings
    if (earth_mesh_.num_triangles() > 0) {
        shader_.Draw(model_matrix, view_matrix, proj_matrix, &earth_mesh_, mat);

    }
}


Point3 Earth::LatLongToSphere(double latitude, double longitude) const {
    // TODO: We recommend filling in this function to put all your
    // lat,long --> sphere calculations in one place.
    float x = cos(GfxMath::ToRadians(latitude))*sin(GfxMath::ToRadians(longitude));
    float y = sin(GfxMath::ToRadians(latitude));
    float z = cos(GfxMath::ToRadians(latitude))*cos(GfxMath::ToRadians(longitude));

    return Point3(x,y,z);
}

Point3 Earth::LatLongToPlane(double latitude, double longitude) const {
    // TODO: We recommend filling in this function to put all your
    // lat,long --> plane calculations in one place.
    double divLat = latitude/90.0;
    divLat = divLat*(M_PI/2.0);
    double divLong = longitude/180.0;
    divLong = divLong*M_PI;
    return Point3(divLong,divLat,0);
}



void Earth::DrawDebugInfo(const Matrix4 &model_matrix, const Matrix4 &view_matrix, const Matrix4 &proj_matrix) {
    // This draws a cylinder for each line segment on each edge of each triangle in your mesh.
    // So it will be very slow if you have a large mesh, but it's quite useful when you are
    // debugging your mesh code, especially if you start with a small mesh.
    for (int t=0; t<earth_mesh_.num_triangles(); t++) {
        std::vector<unsigned int> indices = earth_mesh_.read_triangle_indices_data(t);
        std::vector<Point3> loop;
        loop.push_back(earth_mesh_.read_vertex_data(indices[0]));
        loop.push_back(earth_mesh_.read_vertex_data(indices[1]));
        loop.push_back(earth_mesh_.read_vertex_data(indices[2]));
        quick_shapes_.DrawLines(model_matrix, view_matrix, proj_matrix,
            Color(1,1,0), loop, QuickShapes::LinesType::LINE_LOOP, 0.005f);
    }
}

