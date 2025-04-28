import mpi.MPI;

public class ReciprocalCalculator {
    public static void main(String[] args) {
        // Initialize MPI
        MPI.Init(args);

        int rank = MPI.COMM_WORLD.Rank();    // Current process ID
        int size = MPI.COMM_WORLD.Size();    // Total number of processes

        double[] data = new double[size];    // Array for the root process
        double[] element = new double[1];    // Buffer for sending/receiving one element

        if (rank == 0) {
            // Initialize the array at root
            System.out.println("Original array at root:");
            for (int i = 0; i < size; i++) {
                data[i] = i + 1; // Example: 1, 2, 3, ..., size
                System.out.print(data[i] + " ");
            }
            System.out.println();

            // Send each element to the corresponding process
            for (int i = 1; i < size; i++) {
                element[0] = data[i];
                MPI.COMM_WORLD.Send(element, 0, 1, MPI.DOUBLE, i, 0);
            }

            // Root also processes its own element
            data[0] = 1.0 / data[0];

            // Receive processed data from workers
            for (int i = 1; i < size; i++) {
                MPI.COMM_WORLD.Recv(element, 0, 1, MPI.DOUBLE, i, 1);
                data[i] = element[0];
            }

            // Display the final resultant array
            System.out.println("Resultant array at root (reciprocals):");
            for (int i = 0; i < size; i++) {
                System.out.print(data[i] + " ");
            }
            System.out.println();

        } else {
            // Workers receive an element
            MPI.COMM_WORLD.Recv(element, 0, 1, MPI.DOUBLE, 0, 0);

            // Calculate reciprocal
            element[0] = 1.0 / element[0];

            // Send result back to root
            MPI.COMM_WORLD.Send(element, 0, 1, MPI.DOUBLE, 0, 1);
        }

        // Finalize MPI
        MPI.Finalize();
    }
}