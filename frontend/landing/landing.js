jwt = "";
userInfo ={}; 
// Going to reconsider making this a global, maybe have the server send a json with user info along with jwt and store the whole thing in a cookie

function formatDate(date) {
    const info = new Date(date*1000);
    return (info.getMonth()+1) + "/" + info.getDate() + "/" +info.getFullYear();
}

function populateTable(expenses=[]) {
    let tableData = "";
    for(expense of expenses) {
        const date = formatDate(expense.dateSubmitted);
        const amount = expense.amountInCents/100;
        tableData += `<tr id="expense-${expense.expenseId}"><td>${date}</td><td>${amount}</td><td>${expense.description}</td><td>${expense.status}</td></tr>`;
    }
    document.getElementById("expense-data").innerHTML += tableData;
}

async function populatePage() {
    const userHeaders = await fetch("/users", {
        method:"GET",
        headers:{
            "Authorization":jwt
        }
    });
    const userData = await userHeaders.json();
    userInfo = {"userId":userData.userId, "username":userData.username, "isManager":userData.isManager};
    populateTable(userData.myExpenses);
    if(userData.isManager) {
        document.getElementById("all-expenses").hidden = false;
    }
    
}

async function addExpense(event) {
    event.preventDefault();
    const description = document.getElementById("reason").value;
    if(!description) {
        alert("Description cannot be empty");
        return;
    }
    const amount = document.getElementById("amount").value;
    if(!amount || amount < 0) {
        alert("Amount cannot be empty or less than or equal to zero");
        return;
    }
    // add file url later when I figure that out
    const expenseHeader = await fetch("/users/expense", {
        method:"POST",
        headers:{
            "Content-type":"application/json",
            "Authorization":jwt
        },
        body:JSON.stringify({
            "amountInCents":amount*100,
            "reasonSubmitted":description
        })
    });
    const newExpense = await expenseHeader.json();
    populateTable([newExpense]);
}

async function getAllExpenses() {
    // need to eliminate items in the table to avoid repeats showing up in the table
    document.getElementById("expense-data").innerHTML = "";
    const expenseHeader = await fetch("/users/expense", {
        method:"GET",
        headers:{
            "Authorization":jwt
        }
    });
    const expenseBody = await expenseHeader.json();
    populateTable(expenseBody);
}

document.getElementById("new-expense").addEventListener("submit", addExpense);