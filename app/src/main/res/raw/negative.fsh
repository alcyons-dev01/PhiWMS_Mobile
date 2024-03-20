precision mediump float;

uniform vec3                iResolution;
uniform float               iGlobalTime;
uniform sampler2D           iChannel0;
varying vec2                texCoord;

void mainImage( out vec4 fragColor, in vec2 fragCoord )
{
    vec2 uv = fragCoord.xy;
    vec4 color = texture2D(iChannel0, uv);
    fragColor =  vec4(vec3(1.0) - color.rgb, color.a);
}

void main() {
	mainImage(gl_FragColor, texCoord);
}