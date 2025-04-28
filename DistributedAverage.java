import mpi.MPI;
import java.util.Random;

public class DistributedAverage {
    public static void main(String[] args) throws Exception {
        MPI.Init(args);
        
        int rank = MPI.COMM_WORLD.Rank(); // Get process ID
        int size = MPI.COMM_WORLD.Size(); // Get number of processes

        int unitSize = 5; // Elements for each process
        int totalSize = unitSize * size; // Total elements
        int[] sendBuffer = null;
        int[] receiveBuffer = new int[unitSize];
        double[] partialAverages = new double[size]; // To gather partial averages

        if (rank == 0) {
            // Root process generates random numbers
            sendBuffer = new int[totalSize];
            Random rand = new Random();
            System.out.println("Generated random numbers:");
            for (int i = 0; i < totalSize; i++) {
                sendBuffer[i] = rand.nextInt(100); // Random numbers between 0-99
                System.out.print(sendBuffer[i] + " ");
            }
            System.out.println();
        }

        // Scatter the numbers
        MPI.COMM_WORLD.Scatter(
            sendBuffer, 0, unitSize, MPI.INT,
            receiveBuffer, 0, unitSize, MPI.INT,
            0
        );

        // Each process computes the average of its received numbers
        int localSum = 0;
        for (int num : receiveBuffer) {
            localSum += num;
        }
        double localAverage = (double) localSum / unitSize;
        System.out.println("Process " + rank + " local average: " + localAverage);

        // Gather all local averages to the root process
        MPI.COMM_WORLD.Gather(
            new double[]{localAverage}, 0, 1, MPI.DOUBLE,
            partialAverages, 0, 1, MPI.DOUBLE,
            0
        );

        // Root computes final average
        if (rank == 0) {
            double finalSum = 0;
            for (double avg : partialAverages) {
                finalSum += avg;
            }
            double finalAverage = finalSum / size;
            System.out.println("Final average: " + finalAverage);
        }

        MPI.Finalize();
    }
}