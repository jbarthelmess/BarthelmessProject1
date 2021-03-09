try {
    userInfo = JSON.parse(localStorage.getItem("userInfo"));
} catch {
    alert("You are not authorized to view this page, please sign in");
    window.location.assign("file:///C:/Users/Josh/IdeaProjects/BartProject1/frontend/page.html");
}

base = "http://localhost:7000";
statusValue = {"PENDING":1, "DENIED":2, "APPROVED":3};

function formatDate(date) {
    const info = new Date(date*1000);
    return (info.getMonth()+1) + "/" + info.getDate() + "/" +info.getFullYear();
}

function formatAmount(amount) {
    return new Intl.NumberFormat("en-US",{style:"currency", currency:"USD"}).format(amount);
}

async function getExpense(id) {
    document.getElementById("resolve-form").hidden = true;
    document.getElementById("start-edit").hidden = true;
    document.getElementById("edit-expense").hidden = true;
    const httpRequest = await fetch(base+`/users/expense/${id}`, {
        method:"GET",
        headers:{
            "Authorization":userInfo.jwt
        }
    });
    const fullExpense = await httpRequest.json();
    console.log(fullExpense);
    renderFullExpense(fullExpense);
    // Managers should be able to resolve expenses 
    if(userInfo.isManager && fullExpense.userId !== userInfo.userId && fullExpense.status === "PENDING") {
        document.getElementById("resolve-form").hidden = false;
    }
    if(fullExpense.userId === userInfo.userId && fullExpense.status === "PENDING") {
        document.getElementById("start-edit").hidden = false;
    }
}

// print the full expense in the Expense details area
function renderFullExpense(fullExpense) {
    const detailsList = document.getElementById("expense-details");
    detailsList.hidden = false;
    detailsList.class = `${fullExpense.expenseId}`;
    let listBody = `<li>ExpenseID: ${fullExpense.expenseId} for User ${fullExpense.username}</li>`;
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
    const expenseDataSet = document.getElementById("edit-expense").dataset;
    expenseDataSet.expense = fullExpense.expenseId;
    expenseDataSet.userId = fullExpense.userId;
    document.getElementById("edit-amount").value = fullExpense.amountInCents/100;
    document.getElementById("edit-reason").value = fullExpense.reasonSubmitted;
    detailsList.innerHTML = listBody;
}

/*populates the table */
function populateTable(expenses=[]) {
    let tableData = "";
    for(expense of expenses) {
        const date = formatDate(expense.dateSubmitted);
        const amount = expense.amountInCents/100;
        tableData += `<tr class="info" id="expense-${expense.expenseId}" onclick="getExpense(${expense.expenseId})">`;
        tableData += `<td class="userId" data-value="${expense.userId}">${expense.userId}</td><td class="dateSubmitted" data-value="${expense.dateSubmitted}">${date}</td>`;
        tableData += `<td class="amount" data-value="${amount}">${formatAmount(amount)}</td><td class="status" data-value="${statusValue[expense.status]}">${expense.status}</td></tr>`;
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
    const reason = document.getElementById("resolve-reason").value;
    const newStatus = document.getElementById("resolve-status").value;
    document.getElementById("resolve-status").value = "";
    document.getElementById("resolve-reason").value = "";
    if(!(newStatus === "APPROVED" || newStatus === "DENIED")) {
        alert("Invalid status provided");
        return;
    }
    const expenseId = document.getElementById("expense-details").class;
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
    document.getElementById("resolve-form").hidden = true;
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

async function updateExpense(event) {
    event.preventDefault();
    const verifyData = document.getElementById("edit-expense").dataset;
    if(userInfo.userId !== Number(verifyData.userId)) {
        alert("You are not authorized to update this expense");
        document.getElementById("edit-expense").hidden = true;
        document.getElementById("start-edit").hidden = true;
        return;
    }
    const expenseId = verifyData.expense;
    if(expenseId === 0) {
        console.log("something went wrong");
        return;
    }
    const amount = document.getElementById("edit-amount").value;
    if(!amount || amount < 0) {
        alert("Amount cannot be less than zero");
        return;
    }
    const reason = document.getElementById("edit-reason").value;
    if(!reason) {
        alert("Reason cannot be empty");
        return;
    }
    const httpRequest = await fetch(base+`/users/expense/${expenseId}`,{
        method:"PUT",
        headers:{
            "Content-type":"application/json",
            "Authorization":userInfo.jwt
        },
        body:JSON.stringify({
            "amountInCents":amount*100,
            "reasonSubmitted":reason
        })
    });
    const response = await httpRequest.json();
    document.getElementById(`expense-${expenseId}`).remove();
    populateTable([response]);
    renderFullExpense(response);
    document.getElementById("edit-expense").hidden = true;
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
    document.getElementById("edit-expense-button").addEventListener("click", updateExpense);
    document.getElementById("edit-expense").dataset.userId = userInfo.userId;
    if(userInfo.isManager) {
        document.getElementById("all-expenses").hidden = false;
        document.getElementById("resolve-expense").addEventListener("click", resolveExpense);
    }
    document.getElementById("start-edit").addEventListener("click", (e)=>{
        e.preventDefault();
        document.getElementById("edit-expense").hidden = false;
    })
}
populatePage();

function sortRows(n) {
    let table = document.getElementById("expenses");
    let hasSwitched = true;
    while(hasSwitched) {
        hasSwitched = false;
        let rows = table.rows;
        let shouldSwitch = false;
        let i = 1;
        for(; i< rows.length-1; i++) {
            let top = rows[i].getElementsByTagName("td")[n];
            let bottom = rows[i+1].getElementsByTagName("td")[n];
            if(Number(bottom.dataset.value) < Number(top.dataset.value)) {
                shouldSwitch = true;
                break;
            }
        }
        if(shouldSwitch) {
            rows[i].parentNode.insertBefore(rows[i+1], rows[i]);
            hasSwitched = true;
        }
    }
}