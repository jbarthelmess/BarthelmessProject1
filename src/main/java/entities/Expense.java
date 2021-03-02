package entities;

public class Expense {
    private int expenseId;
    private int userId;
    private int managerHandler;
    private int amountInCents;
    private String reasonSubmitted;
    private String reasonResolved;
    private long dateSubmitted;
    private long dateResolved;
    private ExpenseStatus status;
    private String fileURL;

    public Expense() {
        this.expenseId = 0;
        this.userId = 0;
        this.amountInCents = -1;
        this.dateSubmitted = -1;
        this.dateResolved = 0;
        this.reasonSubmitted = null;
        this.reasonResolved = null;
        this.status = ExpenseStatus.PENDING;
        this.fileURL = null;
    }

    public int getExpenseId() {
        return expenseId;
    }

    public void setExpenseId(int expenseId) {
        if(expenseId <= 0) {
            throw new IllegalArgumentException("Expense ID cannot be less than or equal to zero");
        }
        this.expenseId = expenseId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        if(userId <= 0) {
            throw new IllegalArgumentException("UserId cannot be less than or equal to zero");
        }
        this.userId = userId;
    }

    public int getAmountInCents() {
        return amountInCents;
    }

    public void setAmountInCents(int amountInCents) {
        if(amountInCents < 0) {
            throw new IllegalArgumentException("Amount cannot be less than zero");
        }
        this.amountInCents = amountInCents;
    }

    public String getReasonSubmitted() {
        return reasonSubmitted;
    }

    public void setReasonSubmitted(String reasonSubmitted) {
        if(reasonSubmitted == null) {
            throw new IllegalArgumentException("Reason Submitted cannot be null");
        }
        if(reasonSubmitted.equals("")) {
            throw new IllegalArgumentException("Reason Submitted cannot be empty");
        }
        this.reasonSubmitted = reasonSubmitted;
    }

    public String getReasonResolved() {
        return reasonResolved;
    }

    public void setReasonResolved(String reasonResolved) {
        this.reasonResolved = reasonResolved;
    }

    public long getDateSubmitted() {
        return dateSubmitted;
    }

    public void setDateSubmitted(long dateSubmitted) {
        if(dateSubmitted <= 0) {
            throw new IllegalArgumentException("Date submitted cannot be negative (before 1970)");
        }
        this.dateSubmitted = dateSubmitted;
    }

    public long getDateResolved() {
        return dateResolved;
    }

    public void setDateResolved(long dateResolved) {
        if(dateResolved < this.dateSubmitted && dateResolved != 0) {
            throw new IllegalArgumentException("Date resolved of "+ dateResolved+" cannot be before date submitted of "+this.dateSubmitted);
        }
        this.dateResolved = dateResolved;
    }

    public ExpenseStatus getStatus() {
        return status;
    }

    public String getStatusAsString() {
        return this.status.name();
    }

    public void setStatus(ExpenseStatus status) {
        this.status = status;
    }

    public void setStatusFromString(String status) {
        switch(status) {
            case "PENDING":
                this.status = ExpenseStatus.PENDING;
                break;
            case "APPROVED":
                this.status = ExpenseStatus.APPROVED;
                break;
            case "DENIED":
                this.status = ExpenseStatus.DENIED;
                break;
            default:
                throw new IllegalArgumentException("Invalid status string provided");
        }
    }

    public String getFileURL() {
        return fileURL;
    }

    public void setFileURL(String fileURL) {
        this.fileURL = fileURL;
    }

    public int getManagerHandler() {
        return managerHandler;
    }

    public void setManagerHandler(int managerHandler) {
        this.managerHandler = managerHandler;
    }
}
