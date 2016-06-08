#ifdef GL_ES
    #define LOWP lowp
    precision mediump float;
#else
    #define LOWP
#endif

varying LOWP vec4 v_color;
varying vec2 v_texCoord;

uniform sampler2D u_texture; // diffuse map

void main() {
	gl_FragColor = v_color * texture2D(u_texture, v_texCoord);

    if (gl_FragColor[3] > 0.0) {
        gl_FragColor = vec4(0.0, 0.0, 0.0, 0.75);
    }
}
