# BartProject1

## Dependencies
0. As written, code relies on a PostgreSQL Database server, all other services are built when installed

## Installation Instructions
0. Build using `gradlew fatJar` in the commandline
0. Upload to Server which contains java 
0. Export environment variables
    - `P1_DB_ACCESS="jdbc:postgresql://Database.IP.Address.Here:Port/DatabaseName?user=user&password=password"`
    - `JWT="SecretStringUsedToGenerateAndVerifyJWTs"`
0. Make sure firewall rule is set to allow HTTP traffic
0. Change variable `base` in `login.js` and `landing.js` to `http://VirtualMachineIPAddress:7000`
0. Put all files in `frontend` in a public bucket and change references in `login.js` and `landing.js` to public URLs

## API Notes
#### Endpoints
* `POST /users/login` - login as a user
* `GET /users` - get User Info
* `GET /users/expense/:id` - get full details for expense with given id
* `GET /users/expense` - get all expenses (MANAGERS ONLY)
* `POST /users/expense` - create new expense
* `PUT /users/expense/:id` - update expense with given id
* `GET /users/stats` - get statistics for a manager (MANAGERS ONLY)
#### Notes about endpoints
- All endpoints (except `login`) require a JWT included in the headers of every request under the `Authorization` heading
- Login will return the user a JWT on a successful login which ought to be used in every request
- Update expense has rules about how expenses can be updated by users
    - Regular users can only edit the fields they can submit a request with (Amount Requested and Reason Submitted)
    - Managers cannot edit those fields
    - Managers can change fields the regular users cannot control (Resolution Status, Reason Resolved)
    - Managers must update the status when they update other users expense requests
    - Managers cannot resolve their own expenses and can only edit the regular user fields
- Regular Users can only see their own expenses
- Managers can see all expenses
- Managers can only see their own resolution statistics
