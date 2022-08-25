package net.oneseventhree.game.graphics.utils;

import static java.lang.Math.*;

public class GreedyMeshing
{
    /**
     * Consumes a generated face.
     */
    public interface FaceConsumer {
        /**
         * @param u0 the U coordinate of the minimum corner
         * @param v0 the V coordinate of the minimum corner
         * @param u1 the U coordinate of the maximum corner
         * @param v1 the V coordinate of the maximum corner
         * @param p  the main coordinate of the face (depending on the side)
         * @param s  the side of the face (including positive or negative)
         * @param v  the face value (includes neighbor configuration)
         */
        void consume(int u0, int v0, int u1, int v1, int p, int s, int v);
    }

    /**
     * Pre-computed lookup table for neighbor configurations depending on whether any particular of the
     * three neighbors of the possible four vertices of a face is occupied or not.
     */
    private static final int[] NEIGHBOR_CONFIGS = computeNeighborConfigs();
    /**
     * We limit the length of merged faces to 32, to be able to store 31 as 5 bits.
     */
    private static final int MAX_MERGE_LENGTH = 32;

    private final int[] m;
    private final int dx, dy, dz, ny, py;
    private byte[] vs;
    private int count;

    private static int[] computeNeighborConfigs() {
        int[] offs = new int[256];
        for (int i = 0; i < 256; i++) {
            boolean cxny = (i & 1) == 1, nxny = (i & 1 << 1) == 1 << 1, nxcy = (i & 1 << 2) == 1 << 2, nxpy = (i & 1 << 3) == 1 << 3;
            boolean cxpy = (i & 1 << 4) == 1 << 4, pxpy = (i & 1 << 5) == 1 << 5, pxcy = (i & 1 << 6) == 1 << 6, pxny = (i & 1 << 7) == 1 << 7;
            offs[i] = (cxny ? 1 : 0) + (nxny ? 2 : 0) + (nxcy ? 4 : 0) | (cxny ? 1 : 0) + (pxny ? 2 : 0) + (pxcy ? 4 : 0) << 3
                    | (cxpy ? 1 : 0) + (nxpy ? 2 : 0) + (nxcy ? 4 : 0) << 6 | (cxpy ? 1 : 0) + (pxpy ? 2 : 0) + (pxcy ? 4 : 0) << 9;
            offs[i] = offs[i] << 8;
        }
        return offs;
    }

    public GreedyMeshing(int ny, int py, int dx, int dz) {
        this.dx = dx;
        this.dy = py + 1 - ny;
        this.dz = dz;
        this.ny = ny;
        this.py = py + 1;
        this.m = new int[max(dx, dy) * max(dy, dz)];
    }

    private byte at(int x, int y, int z) {
        return vs[idx(x, y, z)];
    }

    private int idx(int x, int y, int z) {
        return x + 1 + (dx + 2) * (z + 1 + (dz + 2) * (y + 1));
    }

    public int mesh(byte[] vs, FaceConsumer faces) {
        this.vs = vs;
        meshX(faces);
        meshY(faces);
        meshZ(faces);
        return count;
    }

    private void meshX(FaceConsumer faces) {
        for (int x = 0; x < dx;) {
            generateMaskX(x);
            mergeAndGenerateFacesX(faces, ++x);
        }
    }

    private void meshY(FaceConsumer faces) {
        for (int y = ny - 1; y < py;) {
            generateMaskY(y);
            mergeAndGenerateFacesY(faces, ++y);
        }
    }

    private void meshZ(FaceConsumer faces) {
        for (int z = 0; z < dz;) {
            generateMaskZ(z);
            mergeAndGenerateFacesZ(faces, ++z);
        }
    }

    private void generateMaskX(int x) {
        int n = 0;
        for (int z = 0; z < dz; z++)
            for (int y = ny; y < py; y++, n++)
                generateMaskX(x, y, z, n);
    }

    private void generateMaskY(int y) {
        int n = 0;
        for (int x = 0; x < dx; x++)
            for (int z = 0; z < dz; z++, n++)
                generateMaskY(x, y, z, n);
    }

    private void generateMaskZ(int z) {
        int n = 0;
        for (int y = ny; y < py; y++)
            for (int x = 0; x < dx; x++, n++)
                generateMaskZ(x, y, z, n);
    }

    private void generateMaskX(int x, int y, int z, int n) {
        int a = at(x, y, z), b = at(x + 1, y, z);
        if (((a == 0) == (b == 0)))
            m[n] = 0;
        else if (a != 0) {
            m[n] = (a & 0xFF) | neighborsX(x + 1, y, z);
        } else
            m[n] = (b & 0xFF) | neighborsX(x, y, z) | 1 << 31;
    }

    private int neighborsX(int x, int y, int z) {
        /* UV = YZ */
        int n1 = at(x, y - 1, z - 1) != 0 ? 2 : 0;
        int n2 = at(x, y - 1, z) != 0 ? 4 : 0;
        int n3 = at(x, y - 1, z + 1) != 0 ? 8 : 0;
        int n0 = at(x, y, z - 1) != 0 ? 1 : 0;
        int n4 = at(x, y, z + 1) != 0 ? 16 : 0;
        int n7 = at(x, y + 1, z - 1) != 0 ? 128 : 0;
        int n6 = at(x, y + 1, z) != 0 ? 64 : 0;
        int n5 = at(x, y + 1, z + 1) != 0 ? 32 : 0;
        return NEIGHBOR_CONFIGS[n0 | n1 | n2 | n3 | n4 | n5 | n6 | n7];
    }

    private void generateMaskY(int x, int y, int z, int n) {
        int a = at(x, y, z), b = at(x, y + 1, z);
        if (((a == 0) == (b == 0)))
            m[n] = 0;
        else if (a != 0) {
            m[n] = (a & 0xFF) | neighborsY(x, y + 1, z);
        } else
            m[n] = (b & 0xFF) | (y >= 0 ? neighborsY(x, y, z) : 0) | 1 << 31;
    }

    private int neighborsY(int x, int y, int z) {
        /* UV = ZX */
        int n1 = at(x - 1, y, z - 1) != 0 ? 2 : 0;
        int n2 = at(x, y, z - 1) != 0 ? 4 : 0;
        int n3 = at(x + 1, y, z - 1) != 0 ? 8 : 0;
        int n0 = at(x - 1, y, z) != 0 ? 1 : 0;
        int n6 = at(x, y, z + 1) != 0 ? 64 : 0;
        int n4 = at(x + 1, y, z) != 0 ? 16 : 0;
        int n7 = at(x - 1, y, z + 1) != 0 ? 128 : 0;
        int n5 = at(x + 1, y, z + 1) != 0 ? 32 : 0;
        return NEIGHBOR_CONFIGS[n0 | n1 | n2 | n3 | n4 | n5 | n6 | n7];
    }

    private void generateMaskZ(int x, int y, int z, int n) {
        int a = at(x, y, z), b = at(x, y, z + 1);
        if (((a == 0) == (b == 0)))
            m[n] = 0;
        else if (a != 0)
            m[n] = (a & 0xFF) | neighborsZ(x, y, z + 1);
        else
            m[n] = (b & 0xFF) | neighborsZ(x, y, z) | 1 << 31;
    }

    private int neighborsZ(int x, int y, int z) {
        /* UV = XY */
        int n1 = at(x - 1, y - 1, z) != 0 ? 2 : 0;
        int n0 = at(x, y - 1, z) != 0 ? 1 : 0;
        int n7 = at(x + 1, y - 1, z) != 0 ? 128 : 0;
        int n2 = at(x - 1, y, z) != 0 ? 4 : 0;
        int n6 = at(x + 1, y, z) != 0 ? 64 : 0;
        int n3 = at(x - 1, y + 1, z) != 0 ? 8 : 0;
        int n4 = at(x, y + 1, z) != 0 ? 16 : 0;
        int n5 = at(x + 1, y + 1, z) != 0 ? 32 : 0;
        return NEIGHBOR_CONFIGS[n0 | n1 | n2 | n3 | n4 | n5 | n6 | n7];
    }

    private void mergeAndGenerateFacesX(FaceConsumer faces, int x) {
        int i, j, n, incr;
        for (j = 0, n = 0; j < dz; j++)
            for (i = ny; i < py; i += incr, n += incr)
                incr = mergeAndGenerateFaceX(faces, x, n, i, j);
    }

    private void mergeAndGenerateFacesY(FaceConsumer faces, int y) {
        int i, j, n, incr;
        for (j = 0, n = 0; j < dx; j++)
            for (i = 0; i < dz; i += incr, n += incr)
                incr = mergeAndGenerateFaceY(faces, y, n, i, j);
    }

    private void mergeAndGenerateFacesZ(FaceConsumer faces, int z) {
        int i, j, n, incr;
        for (j = ny, n = 0; j < py; j++)
            for (i = 0; i < dx; i += incr, n += incr)
                incr = mergeAndGenerateFaceZ(faces, z, n, i, j);
    }

    private int mergeAndGenerateFaceX(FaceConsumer faces, int x, int n, int i, int j) {
        int mn = m[n];
        if (mn == 0)
            return 1;
        int w = determineWidthX(mn, n, i);
        int h = determineHeightX(mn, n, j, w);
        faces.consume(i, j, i + w, j + h, x, mn > 0 ? 1 : 0, mn);
        count++;
        eraseMask(n, w, h, dy);
        return w;
    }

    private int mergeAndGenerateFaceY(FaceConsumer faces, int y, int n, int i, int j) {
        int mn = m[n];
        if (mn == 0)
            return 1;
        int w = determineWidthY(mn, n, i);
        int h = determineHeightY(mn, n, j, w);
        faces.consume(i, j, i + w, j + h, y, 2 + (mn > 0 ? 1 : 0), mn);
        count++;
        eraseMask(n, w, h, dz);
        return w;
    }

    private int mergeAndGenerateFaceZ(FaceConsumer faces, int z, int n, int i, int j) {
        int mn = m[n];
        if (mn == 0)
            return 1;
        int w = determineWidthZ(mn, n, i);
        int h = determineHeightZ(mn, n, j, w);
        faces.consume(i, j, i + w, j + h, z, 4 + (mn > 0 ? 1 : 0), mn);
        count++;
        eraseMask(n, w, h, dx);
        return w;
    }

    private void eraseMask(int n, int w, int h, int d) {
        for (int l = 0, ls = 0; l < h; l++, ls += d)
            for (int k = 0; k < w; k++)
                m[n + k + ls] = 0;
    }

    private int determineWidthX(int c, int n, int i) {
        int w = 1;
        for (; w < MAX_MERGE_LENGTH && i + w < py && c == m[n + w]; w++)
            ;
        return w;
    }

    private int determineWidthY(int c, int n, int i) {
        int w = 1;
        for (; i + w < dz && c == m[n + w]; w++)
            ;
        return w;
    }

    private int determineWidthZ(int c, int n, int i) {
        int w = 1;
        for (; i + w < dx && c == m[n + w]; w++)
            ;
        return w;
    }

    private int determineHeightX(int c, int n, int j, int w) {
        int h = 1;
        for (int hs = dy; j + h < dz; h++, hs += dy)
            for (int k = 0; k < w; k++)
                if (c != m[n + k + hs])
                    return h;
        return h;
    }

    private int determineHeightY(int c, int n, int j, int w) {
        int h = 1;
        for (int hs = dz; j + h < dx; h++, hs += dz)
            for (int k = 0; k < w; k++)
                if (c != m[n + k + hs])
                    return h;
        return h;
    }

    private int determineHeightZ(int c, int n, int j, int w) {
        int h = 1;
        for (int hs = dx; h < MAX_MERGE_LENGTH && j + h < py; h++, hs += dx)
            for (int k = 0; k < w; k++)
                if (c != m[n + k + hs])
                    return h;
        return h;
    }
}
