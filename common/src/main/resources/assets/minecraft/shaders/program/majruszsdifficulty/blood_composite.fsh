#version 150

uniform sampler2D DiffuseSampler;
uniform sampler2D BloodSampler;

uniform vec2 MaskSize;
uniform float AgeOffset;

in vec2 texCoord;

out vec4 fragColor;

void main() {
    vec2 uv = clamp(texCoord, vec2(0.0), vec2(1.0));
    vec4 material = texture(BloodSampler, uv);
    float lifetime = clamp(1.0 - material.a - AgeOffset, 0.0, 1.0);
    float fade = lifetime * lifetime;
    float opacity = material.r * fade * 0.82;

    if (opacity <= 0.001) {
        fragColor = vec4(texture(DiffuseSampler, uv).rgb, 1.0);
        return;
    }

    vec2 texel = MaskSize;
    float heightLeft = texture(BloodSampler, clamp(uv - vec2(texel.x, 0.0), vec2(0.0), vec2(1.0))).g;
    float heightRight = texture(BloodSampler, clamp(uv + vec2(texel.x, 0.0), vec2(0.0), vec2(1.0))).g;
    float heightDown = texture(BloodSampler, clamp(uv - vec2(0.0, texel.y), vec2(0.0), vec2(1.0))).g;
    float heightUp = texture(BloodSampler, clamp(uv + vec2(0.0, texel.y), vec2(0.0), vec2(1.0))).g;
    vec3 normal = normalize(vec3(
        (heightLeft - heightRight) * 2.2,
        (heightDown - heightUp) * 2.2,
        0.82
    ));

    float freshness = clamp(material.b * (0.42 + lifetime * 0.72), 0.0, 1.0);
    float body = smoothstep(0.04, 0.72, material.r);
    vec2 refraction = normal.xy * opacity * mix(0.00035, 0.0012, freshness);
    vec3 scene = texture(DiffuseSampler, clamp(uv + refraction, vec2(0.0), vec2(1.0))).rgb;

    vec3 driedBlood = vec3(0.064, 0.002, 0.0012);
    vec3 freshBlood = vec3(0.245, 0.0045, 0.003);
    vec3 albedo = mix(driedBlood, freshBlood, freshness);
    albedo *= mix(0.64, 1.04, body);
    albedo *= 0.94 + material.g * 0.07 + (material.b - 0.5) * 0.035;

    vec3 lightDirection = normalize(vec3(-0.42, 0.55, 0.78));
    vec3 viewDirection = vec3(0.0, 0.0, 1.0);
    vec3 halfDirection = normalize(lightDirection + viewDirection);
    float diffuse = max(dot(normal, lightDirection), 0.0);
    float specularPower = mix(20.0, 54.0, freshness);
    float specular = pow(max(dot(normal, halfDirection), 0.0), specularPower);
    specular *= mix(0.025, 0.34, freshness) * material.g * fade;

    float edgeRing = (1.0 - body) * material.r;
    vec3 litBlood = albedo * (0.42 + diffuse * 0.58);
    litBlood *= 1.0 - edgeRing * mix(0.38, 0.12, freshness);
    litBlood += vec3(1.0, 0.34, 0.22) * specular;

    fragColor = vec4(mix(scene, litBlood, opacity), 1.0);
}
