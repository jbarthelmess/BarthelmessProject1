try {
    userInfo = JSON.parse(localStorage.getItem("userInfo"));
    console.log(userInfo);
} catch {
    alert("You are not authorized to view this page, please sign in");
    window.location.assign("file:///C:/Users/Josh/IdeaProjects/BartProject1/frontend/page.html");
}

base = "http://localhost:7000";

function formatDate(date) {
    const info = new Date(date*1000);
    return (info.getMonth()+1) + "/" + info.getDate() + "/" +info.getFullYear();
}

function formatAmount(amount) {
    return new Intl.NumberFormat("en-US",{style:"currency", currency:"USD"}).format(amount);
}

async function getExpense(id) {
    document.getElementById("resolve-form").hidden = true;
    const httpRequest = await fetch(base+`/users/expense/${id}`, {
        method:"GET",
        headers:{
            "Authorization":userInfo.jwt
        }
    });
    const fullExpense = await httpRequest.json();
    renderFullExpense(fullExpense);
    // Managers should be able to resolve expenses 
    if(userInfo.isManager && fullExpense.userId !== userInfo.userId && fullExpense.status === "PENDING") {
        document.getElementById("resolve-form").hidden = false;
    }
}

// print the full expense in the Expense details area
function renderFullExpense(fullExpense) {
    const detailsList = document.getElementById("expense-details");
    detailsList.hidden = false;
    detailsList.class = `${fullExpense.expenseId}`;
    let listBody = `<li>ExpenseID: ${fullExpense.expenseId} for User ${fullExpense.userId}</li>`;
    listBody += `<li>${formatAmount(fullExpense.amountInCents/100)} requested on ${formatDate(fullExpense.dateSubmitted)} Status: ${fullExpense.status}</li>`;
    listBody += `<li>Reason Submitted: ${fullExpense.reasonSubmitted}</li>`;
    if(!(fullExpense.status === "PENDING")) {
        listBody += `<li>Resolved by Manager ${fullExpense.managerHandler} on ${formatDate(fullExpense.dateResolved)}</li>`;
        if(fullExpense.reasonResolved) {
            listBody += `<li>Reason Resolved: ${fullExpense.reasonResolved}</li>`;
        }
    }
    if(fullExpense.fileURL) {
        listBody+= `<li>Attached Reference File: <a href="${fullExpense.fileURL}">${fullExpense.fileURL}</a></li>`;
    }
    detailsList.innerHTML = listBody;
}

/*populates the table */
function populateTable(expenses=[]) {
    let tableData = "";
    for(expense of expenses) {
        const date = formatDate(expense.dateSubmitted);
        const amount = expense.amountInCents/100;
        tableData += `<tr id="expense-${expense.expenseId}" onclick="getExpense(${expense.expenseId})">`;
        tableData += `<td>${expense.userId}</td><td>${date}</td><td>${formatAmount(amount)}</td><td>${expense.status}</td></tr>`;
    }
    document.getElementById("expense-data").innerHTML += tableData;
}

async function getAllExpenses() {
    // need to eliminate items in the table to avoid repeats showing up in the table
    if(!userInfo.isManager) {
        alert("You are not authorized to view all expenses");
        return;
    }
    const expenseHeader = await fetch(base+"/users/expense", {
        method:"GET",
        headers:{
            "Authorization":userInfo.jwt
        }
    });
    const expenseBody = await expenseHeader.json();
    document.getElementById("expense-data").innerHTML = "";
    populateTable(expenseBody);
}

async function resolveExpense() {
    if(!userInfo.isManager) {
        alert("You are not authorized to resolve expenses");
        return;
    }
    const newStatus = document.getElementById("resolve-status").value;
    if(!(newStatus === "APPROVED" || newStatus === "DENIED")) {
        alert("Invalid status provided");
        return;
    }
    const reason = document.getElementById("resolve-reason").value;
    const expenseId = document.getElementById("expense-details").class;
    console.log("resolving expense "+expenseId);
    console.log("status: "+newStatus);
    console.log("reason: "+reason);
    const httpRequest = await fetch(base+"/users/expense/"+expenseId, {
        method:"PUT",
        headers:{
            "Content-type":"application/json",
            "Authorization":userInfo.jwt
        },
        body:JSON.stringify({
            "status":newStatus,
            "reasonResolved":reason
        })
    });
    const updated = await httpRequest.json();
    document.getElementById("expense-"+expenseId).remove();
    renderFullExpense(updated);
    populateTable([updated]);
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
    document.getElementById("reason").value = "";
    document.getElementById("amount").value = "";
    // add file url later when I figure that out
    const expenseHeader = await fetch(base+"/users/expense", {
        method:"POST",
        headers:{
            "Content-type":"application/json",
            "Authorization":userInfo.jwt
        },
        body:JSON.stringify({
            "amountInCents":Math.round(amount*100),
            "reasonSubmitted":description
        })
    });
    const newExpense = await expenseHeader.json();
    console.log(newExpense);
    populateTable([newExpense]);
}

// populate the page with info
async function populatePage() {
    const userHeaders = await fetch(base+"/users", {
        method:"GET",
        headers:{
            "Authorization":userInfo.jwt
        }
    });
    const userData = await userHeaders.json();
    populateTable(userData.myExpenses);
    document.getElementById("new-expense-button").addEventListener("click", addExpense);
    if(userInfo.isManager) {
        document.getElementById("all-expenses").hidden = false;
        document.getElementById("resolve-expense").addEventListener("click", resolveExpense);
    }
}
populatePage();