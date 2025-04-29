package rgbmatrix;

import java.util.ArrayList;

public class Clustering {
    public static Double[][][][] cluster(Integer k, ArrayList<RGBMatrix> blocks, Integer n, Integer m, Double Threshold)
    {
        if (k > blocks.size()) return null;

        Double[][][][] centroids = new Double[k][n][m][3];
        ArrayList<ArrayList<RGBMatrix>> clusters = new ArrayList<ArrayList<RGBMatrix>>();
        ArrayList<RGBMatrix> temp = new ArrayList<RGBMatrix>();
        temp.add(blocks.get(0));
        for (Integer i = 0; i < k; ++i)
        {
            temp.set(0, blocks.get(i));
            centroids[i] = RGBMatrix.mean(temp, m, n);
            clusters.add(new ArrayList<RGBMatrix>());
        }
        Double lastMeanDistance = -1.;
        Integer iteration = 1;
        while (true)
        {
            Double totalDistance = 0.;
            Double totalMSE = 0.;
            for (Integer i = 0; i < k; ++i)
            {
                clusters.get(i).clear();
            }
            for (Integer b = 0; b < blocks.size(); ++b)
            {
                Double minDistance = blocks.get(b).ecludianDistance(centroids[0], n, m);
                Integer minI = 0;
                for (Integer i = 1; i < k; ++i)
                {
                    Double distance =  blocks.get(b).ecludianDistance(centroids[i], n, m);
                    if (distance < minDistance)
                    {
                        minDistance = distance;
                        minI = i;
                    }
                }
                clusters.get(minI).add(blocks.get(b));
                totalDistance += minDistance;
                totalMSE += blocks.get(b).mse(centroids[minI], m, n);
            }
            for (Integer i = 0; i < k; ++i)
            {
                centroids[i] = RGBMatrix.mean(clusters.get(i), m, n);
            }
            System.out.println("Iteration [" + iteration + "]:");
            System.out.println("Mean distance: " + totalDistance / blocks.size());
            System.out.println("MSE: " + totalMSE / blocks.size());
            if (Math.abs(totalDistance / blocks.size() - lastMeanDistance) < Threshold) 
            {
                break;
            }
            lastMeanDistance = totalDistance / blocks.size();
            ++iteration;

        }
        return centroids;
    }
}
