package com.jqh.gpuimagelib.render.filter;

import android.content.Context;
import android.graphics.PointF;
import android.opengl.GLES20;

public class GPUImageGlassSphereFilter extends BaseGPUImageFilter {

    public static final String SPHERE_FRAGMENT_SHADER = "" +
            "varying highp vec2 ft_Position;\n" +
            "\n" +
            "uniform sampler2D inputImageTexture;\n" +
            "\n" +
            "uniform highp vec2 center;\n" +
            "uniform highp float radius;\n" +
            "uniform highp float aspectRatio;\n" +
            "uniform highp float refractiveIndex;\n" +
            "// uniform vec3 lightPosition;\n" +
            "const highp vec3 lightPosition = vec3(-0.5, 0.5, 1.0);\n" +
            "const highp vec3 ambientLightPosition = vec3(0.0, 0.0, 1.0);\n" +
            "\n" +
            "void main()\n" +
            "{\n" +
            "highp vec2 textureCoordinateToUse = vec2(ft_Position.x, (ft_Position.y * aspectRatio + 0.5 - 0.5 * aspectRatio));\n" +
            "highp float distanceFromCenter = distance(center, textureCoordinateToUse);\n" +
            "lowp float checkForPresenceWithinSphere = step(distanceFromCenter, radius);\n" +
            "\n" +
            "distanceFromCenter = distanceFromCenter / radius;\n" +
            "\n" +
            "highp float normalizedDepth = radius * sqrt(1.0 - distanceFromCenter * distanceFromCenter);\n" +
            "highp vec3 sphereNormal = normalize(vec3(textureCoordinateToUse - center, normalizedDepth));\n" +
            "\n" +
            "highp vec3 refractedVector = 2.0 * refract(vec3(0.0, 0.0, -1.0), sphereNormal, refractiveIndex);\n" +
            "refractedVector.xy = -refractedVector.xy;\n" +
            "\n" +
            "highp vec3 finalSphereColor = texture2D(inputImageTexture, (refractedVector.xy + 1.0) * 0.5).rgb;\n" +
            "\n" +
            "// Grazing angle lighting\n" +
            "highp float lightingIntensity = 2.5 * (1.0 - pow(clamp(dot(ambientLightPosition, sphereNormal), 0.0, 1.0), 0.25));\n" +
            "finalSphereColor += lightingIntensity;\n" +
            "\n" +
            "// Specular lighting\n" +
            "lightingIntensity  = clamp(dot(normalize(lightPosition), sphereNormal), 0.0, 1.0);\n" +
            "lightingIntensity  = pow(lightingIntensity, 15.0);\n" +
            "finalSphereColor += vec3(0.8, 0.8, 0.8) * lightingIntensity;\n" +
            "\n" +
            "gl_FragColor = vec4(finalSphereColor, 1.0) * checkForPresenceWithinSphere;\n" +
            "}\n";

    private PointF center;
    private int centerLocation;
    private float radius;
    private int radiusLocation;
    private float aspectRatio;
    private int aspectRatioLocation;
    private float refractiveIndex;
    private int refractiveIndexLocation;

    public GPUImageGlassSphereFilter(Context context) {
        this(context, new PointF(0.5f, 0.5f), 0.25f, 0.71f);
    }

    public GPUImageGlassSphereFilter(Context context, PointF center, float radius, float refractiveIndex) {
        super(context);
        this.center = center;
        this.radius = radius;
        this.refractiveIndex = refractiveIndex;
    }


    @Override
    protected String getFragmentSource() {
        return SPHERE_FRAGMENT_SHADER;
    }

    @Override
    public void init() {
        if (inited) return;
        super.init();
        centerLocation = GLES20.glGetUniformLocation(getProgram(), "center");
        radiusLocation = GLES20.glGetUniformLocation(getProgram(), "radius");
        aspectRatioLocation = GLES20.glGetUniformLocation(getProgram(), "aspectRatio");
        refractiveIndexLocation = GLES20.glGetUniformLocation(getProgram(), "refractiveIndex");
    }

    @Override
    public void update() {
        if (!isNeedUpdate) return;
        super.update();
        setFloat(refractiveIndexLocation, refractiveIndex);
        setPoint(centerLocation, center);
        setFloat(radiusLocation, radius);
    }

    @Override
    public void onOutputSizeChanged(int width, int height) {
        if (!isNeedChangeWH) return;
        super.onOutputSizeChanged(width, height);
        aspectRatio = (float) height / width;
        setFloat(aspectRatioLocation, aspectRatio);
        super.onOutputSizeChanged(width, height);
    }
}
