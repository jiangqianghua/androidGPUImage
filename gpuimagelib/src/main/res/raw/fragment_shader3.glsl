precision mediump float;
varying vec2 ft_Position;
uniform sampler2D sTexture;
void main() {
    lowp vec4 textureColor = texture2D(sTexture, ft_Position);
    gl_FragColor = vec4((textureColor.rgb + vec3(-0.5)), textureColor.w);
//    float r = textureColor.r * 0.1;
//    float g = textureColor.g  ;
//    float b = textureColor.b ;
//    float r = textureColor.r * 0.2125 ;
//    float g = textureColor.r * 0.2125 + textureColor.g * 0.7154 + textureColor.b * 0.0721; ;
//    float b = textureColor.r * 0.2125 + textureColor.g * 0.7154 + textureColor.b * 0.0721; ;


//    gl_FragColor = vec4(r,g,b, textureColor.w);
}
