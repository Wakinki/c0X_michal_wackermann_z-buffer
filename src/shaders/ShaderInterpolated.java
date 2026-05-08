package shaders;

import objectdata.Vertex;
import transforms.Col;

public class ShaderInterpolated implements Shader {
    @Override
    public Col getColor(Vertex a, Vertex b, Vertex c, double wA, double wB, double wC) {
        return a.getCol().mul(wA)
                .add(b.getCol().mul(wB))
                .add(c.getCol().mul(wC));
    }
}