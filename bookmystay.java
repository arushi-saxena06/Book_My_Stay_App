import java.util.*;

/**
 * ============================================================
 * APPLICATION - bookmystay
 * ============================================================
 *
 * Use Case 10: Booking Cancellation & Inventory Rollback
 *
 * @version 10.0
 */
public class bookmystay {

    public static void main(String[] args) {

        System.out.println("Booking Cancellation\n");

        // Initialize Inventory
        RoomInventory inventory = new RoomInventory();
        inventory.addRoomType("Single", 5);

        // Cancellation Service
        CancellationService service = new CancellationService();

        // Simulate confirmed booking
        String reservationId = "Single-1";
        service.registerBooking(reservationId, "Single");

        // Cancel booking
        service.cancelBooking(reservationId, inventory);

        // Show rollback history
        service.showRollbackHistory();

        // Show updated inventory
        System.out.println("\nUpdated Single Room Availability: "
                + inventory.getAvailableRooms("Single"));
    }
}

/* ============================================================
 * CLASS - RoomInventory
 * ============================================================ */
class RoomInventory {

    private Map<String, Integer> inventory;

    public RoomInventory() {
        inventory = new HashMap<>();
    }

    public void addRoomType(String type, int count) {
        inventory.put(type, count);
    }

    public void increaseInventory(String type) {
        inventory.put(type, inventory.getOrDefault(type, 0) + 1);
    }

    public int getAvailableRooms(String type) {
        return inventory.getOrDefault(type, 0);
    }
}

/* ============================================================
 * CLASS - CancellationService
 * ============================================================
 *
 * Handles booking cancellation and rollback logic
 *
 * @version 10.0
 */
class CancellationService {

    // Stack for rollback tracking (LIFO)
    private Stack<String> releasedRoomIds;

    // Map reservationId -> roomType
    private Map<String, String> reservationRoomTypeMap;

    public CancellationService() {
        releasedRoomIds = new Stack<>();
        reservationRoomTypeMap = new HashMap<>();
    }

    // Register confirmed booking
    public void registerBooking(String reservationId, String roomType) {
        reservationRoomTypeMap.put(reservationId, roomType);
    }

    // Cancel booking
    public void cancelBooking(String reservationId, RoomInventory inventory) {

        // Validate reservation
        if (!reservationRoomTypeMap.containsKey(reservationId)) {
            System.out.println("Invalid cancellation request. Reservation not found.");
            return;
        }

        String roomType = reservationRoomTypeMap.get(reservationId);

        // Push to rollback stack
        releasedRoomIds.push(reservationId);

        // Restore inventory
        inventory.increaseInventory(roomType);

        // Remove booking
        reservationRoomTypeMap.remove(reservationId);

        System.out.println("Booking cancelled successfully. Inventory restored for room type: " + roomType);
    }

    // Display rollback history
    public void showRollbackHistory() {

        System.out.println("\nRollback History (Most Recent First):");

        if (releasedRoomIds.isEmpty()) {
            System.out.println("No cancellations yet.");
            return;
        }

        for (int i = releasedRoomIds.size() - 1; i >= 0; i--) {
            System.out.println("Released Reservation ID: " + releasedRoomIds.get(i));
        }
    }
}