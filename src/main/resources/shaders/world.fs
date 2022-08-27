#version 330

out vec4 outColour;
in vec2 passTextureCoord;

uniform sampler2D texSampler;

void main()
{
    gl_FragColor = texture(texSampler, passTextureCoord);
}