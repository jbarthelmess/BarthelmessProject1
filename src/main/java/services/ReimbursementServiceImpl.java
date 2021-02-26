package services;

import daos.ReimbursementDAO;
import entities.User;

public class ReimbursementServiceImpl implements ReimbursementService{
    private ReimbursementDAO dao;

    public ReimbursementServiceImpl(ReimbursementDAO dao) {
        if(dao == null) {
            throw new IllegalArgumentException("ReimbursementDAO cannot be null");
        }
        this.dao = dao;
    }
    @Override
    public User getUser(User user) {
        return null;
    }
}
