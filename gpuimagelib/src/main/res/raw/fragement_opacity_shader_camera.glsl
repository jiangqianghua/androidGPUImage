
#extension GL_OES_EGL_image_external : require
precision mediump float;
varying vec2 ft_Position;
uniform samplerExternalOES sTexture;
uniform lowp float opacity;

void main()
    {
         lowp vec4 textureColor = texture2D(sTexture, ft_Position);
         gl_FragColor = vec4(textureColor.rgb * opacity, textureColor.w);
    }