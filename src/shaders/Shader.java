package shaders;

import objectdata.Vertex;
import transforms.Col;

@FunctionalInterface
public interface Shader {
    Col getColor(Vertex a, Vertex b, Vertex c, double wA, double wB, double wC);
}