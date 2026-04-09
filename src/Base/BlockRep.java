package Base;

public class BlockRep extends Resident {
    public BlockRep(String name, String email, String username, String password, String roomNumber) {
        super(name, email, username, password, roomNumber);
        setRole("blockrep");
    }

    @Override
    public String toString() {
        return "Block Rep: " + getUsername() + ", Room: " + getRoomNumber() + ", Role: " + getRole();
    }
}
