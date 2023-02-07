#version 330

in vec3 position_in_eye_space;
in vec3 normal_in_eye_space;

out vec4 color;

uniform vec3 light_in_eye_space;
uniform vec4 Ia, Id, Is;

uniform vec4 ka, kd, ks;
uniform float s;


void main() {
    vec3 l = normalize(light_in_eye_space-position_in_eye_space);
    vec3 n = normalize(normal_in_eye_space);
    vec3 e = normalize(-position_in_eye_space);
    vec3 h = normalize(normalize(l)+normalize(e));
    color = ka*Ia + kd*Id*(max(dot(l, n),0)) + ks*Is*pow(max(dot(h, n),0), s);
}
