package entities;

public class Expense {
    private int userId;
    private int amountInCents;
    private String reasonSubmitted;
    private String reasonResolved;
    private long dateSubmitted;
    private long dateResolved;
    private ExpenseStatus status;
    private String fileURL;

    public Expense() {
        this.userId = 0;
        this.amountInCents = -1;
        this.dateSubmitted = -1;
        this.dateResolved = -2;
        this.reasonSubmitted = null;
        this.reasonResolved = null;
        this.status = null;
        this.fileURL = null;
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
        if(dateResolved < this.dateSubmitted) {
            throw new IllegalArgumentException("Date resolved cannot be before date submitted");
        }
        this.dateResolved = dateResolved;
    }

    public ExpenseStatus getStatus() {
        return status;
    }

    public void setStatus(ExpenseStatus status) {
        this.status = status;
    }

    public String getFileURL() {
        return fileURL;
    }

    public void setFileURL(String fileURL) {
        this.fileURL = fileURL;
    }
}
