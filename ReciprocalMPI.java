import mpi.MPI;

public class ReciprocalMPI {
    public static void main(String[] args) throws Exception {
        MPI.Init(args);

        int rank = MPI.COMM_WORLD.Rank();
        int size = MPI.COMM_WORLD.Size();
        
        int root = 0;
        int send_buffer[] = null;
        int recieve_buffer[] = new int[1];
        double new_recieve_buffer[] = new double[size];

        // Root process initializes the array
        if (rank == root) {
            send_buffer = new int[size];
            System.out.println("Elements at root process:");
            for (int i = 0; i < size; i++) {
                send_buffer[i] = i + 1; // You can change to user input if you want
                System.out.println("Element " + i + " = " + send_buffer[i]);
            }
        }

        // Scatter one element to each process
        MPI.COMM_WORLD.Scatter(
            send_buffer,
            0,
            1,
            MPI.INT,
            recieve_buffer,
            0,
            1,
            MPI.INT,
            root
        );

        // Each process calculates the reciprocal
        double reciprocal = 1.0 / recieve_buffer[0];
        System.out.println("Process " + rank + " received " + recieve_buffer[0] + " and calculated reciprocal = " + reciprocal);

        // Gather all reciprocals at root process
        MPI.COMM_WORLD.Gather(
            new double[]{reciprocal},
            0,
            1,
            MPI.DOUBLE,
            new_recieve_buffer,
            0,
            1,
            MPI.DOUBLE,
            root
        );

        // Root displays the final reciprocal array
        if (rank == root) {
            System.out.println("\nFinal Reciprocal Array at Root:");
            for (int i = 0; i < size; i++) {
                System.out.println("Reciprocal of element " + send_buffer[i] + " = " + new_recieve_buffer[i]);
            }
        }

        MPI.Finalize();
    }
}