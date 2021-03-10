package entities;

public class ManagerStatistics {
    private int totalReimbursed;
    private int approvedCount;
    private int deniedCount;
    private int userId;

    public ManagerStatistics() {
        this.userId = 0;
        this.approvedCount = 0;
        this.deniedCount =0;
        this.totalReimbursed = 0;
    }

    public int getTotalReimbursed() {
        return totalReimbursed;
    }

    public void setTotalReimbursed(int totalReimbursed) {
        this.totalReimbursed = totalReimbursed;
    }

    public int getApprovedCount() {
        return approvedCount;
    }

    public void setApprovedCount(int approvedCount) {
        this.approvedCount = approvedCount;
    }

    public int getDeniedCount() {
        return deniedCount;
    }

    public void setDeniedCount(int deniedCount) {
        this.deniedCount = deniedCount;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "ManagerStatistics{" +
                "totalReimbursed=" + totalReimbursed +
                ", approvedCount=" + approvedCount +
                ", deniedCount=" + deniedCount +
                ", userId=" + userId +
                '}';
    }
}
