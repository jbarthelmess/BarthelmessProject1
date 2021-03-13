try {
    userInfo = JSON.parse(sessionStorage.getItem("userInfo"));
} catch {
    window.location.assign("file:///C:/Users/Josh/IdeaProjects/BartProject1/frontend/page.html");
}
//base = "http://localhost:7000";
base = "http://35.202.96.201:7000";
statusValue = {"PENDING":1, "DENIED":2, "APPROVED":3};

function formatDate(date) {
    const info = new Date(date*1000);
    return (info.getMonth()+1) + "/" + info.getDate() + "/" +info.getFullYear();
}

function formatAmount(amount) {
    return new Intl.NumberFormat("en-US",{style:"currency", currency:"USD"}).format(amount);
}

function formatPercent(percent) {
    return String(Math.round(percent*100))+"%";
}
async function getExpense(id) {
    const lastSelected = [...document.getElementsByClassName("selected")];
    for(let i of lastSelected) {
        i.classList.toggle("selected");
    }
    const row = document.getElementById(`expense-${id}`);
    row.classList.add("selected");
    const httpRequest = await fetch(base+`/users/expense/${id}`, {
        method:"GET",
        headers:{
            "Authorization":userInfo.jwt
        }
    });
    const fullExpense = await httpRequest.json();
    document.getElementById("resolve-form").hidden = true;
    document.getElementById("start-edit").hidden = true;
    document.getElementById("edit-expense").hidden = true;
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
    detailsList.dataset.amount = fullExpense.amountInCents;
    detailsList.class = `${fullExpense.expenseId}`;
    let listBody = `<li id="full-expense-owner" data-name="${fullExpense.username}">Expense Submitted by ${fullExpense.username}</li>`;
    listBody += `<li>${formatAmount(fullExpense.amountInCents/100)} requested on ${formatDate(fullExpense.dateSubmitted)} Status: ${fullExpense.status}</li>`;
    listBody += `<li>Reason Submitted: ${fullExpense.reasonSubmitted}</li>`;
    if(!(fullExpense.status === "PENDING")) {
        listBody += `<li>Resolved by Manager ${fullExpense.managerHandler} on ${formatDate(fullExpense.dateResolved)}</li>`;
        if(fullExpense.reasonResolved) {
            listBody += `<li>Reason Resolved: ${fullExpense.reasonResolved}</li>`;
        }
    }
    /*
    if(fullExpense.fileURL) {
        listBody+= `<li>Attached Reference File: <a href="${fullExpense.fileURL}">Download</a></li>`;
    }
    */
    const expenseDataSet = document.getElementById("edit-expense").dataset;
    expenseDataSet.expense = fullExpense.expenseId;
    expenseDataSet.userId = fullExpense.userId;
    document.getElementById("edit-amount").value = fullExpense.amountInCents/100;
    document.getElementById("edit-reason").value = fullExpense.reasonSubmitted;
    detailsList.innerHTML = listBody;
}

/*populates the table */
function populateTable(expenses=[], highlight=false) {
    let tableData = "";
    for(expense of expenses) {
        const date = formatDate(expense.dateSubmitted);
        const amount = expense.amountInCents/100;
        tableData += `<tr style="cursor: pointer;" class="info${highlight? " selected": ""}" id="expense-${expense.expenseId}" onclick="getExpense(${expense.expenseId})">`;
        tableData += `<td class="userId" data-value="${expense.userId}">${expense.username}</td><td class="dateSubmitted" data-value="${expense.dateSubmitted}">${date}</td>`;
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
    document.getElementById("expense-details").innerHTML = "";
    populateTable(expenseBody);
    document.getElementById("all-expenses").hidden = true;
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
    updated.username = document.getElementById("full-expense-owner").dataset.name;
    renderFullExpense(updated);
    populateTable([updated], true);
    const stats = document.getElementById("stats-summary").dataset;
    if(newStatus === "APPROVED") {
        stats.approvedCount = Number(stats.approvedCount)+1;
        stats.totalReimbursed = Number(stats.totalReimbursed)+Number(document.getElementById("expense-details").dataset.amount);
    } else {
        stats.deniedCount = Number(stats.deniedCount)+1;
    }
    calculateStats(stats);
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
    let bodyJSON = {
        "amountInCents":Math.round(amount*100),
        "reasonSubmitted":description
    };
    /*
    const filePath = document.getElementById("file-upload").files[0];
    if(filePath) {
        const url = await uploadFile(filePath);
        bodyJSON.fileURL = url;
        document.getElementById("file-upload").value = "";
    }
    */
    const expenseHeader = await fetch(base+"/users/expense", {
        method:"POST",
        headers:{
            "Content-type":"application/json",
            "Authorization":userInfo.jwt
        },
        body:JSON.stringify(bodyJSON)
    });
    const newExpense = await expenseHeader.json();
    newExpense.username = userInfo.username;
    populateTable([newExpense], true);
    renderFullExpense(newExpense);
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
    let bodyJSON = {
        "amountInCents":amount*100,
        "reasonSubmitted":reason
    };
    /*
    const filePath = document.getElementById("edit-file-upload").files[0];
    if(filePath) {
        const url = await uploadFile(filePath);
        bodyJSON.fileURL = url;
        document.getElementById("edit-file-upload").value = "";
    }
    */
    const httpRequest = await fetch(base+`/users/expense/${expenseId}`,{
        method:"PUT",
        headers:{
            "Content-type":"application/json",
            "Authorization":userInfo.jwt
        },
        body:JSON.stringify(bodyJSON)
    });
    const response = await httpRequest.json();
    response.username = userInfo.username;
    document.getElementById(`expense-${expenseId}`).remove();
    populateTable([response], true);
    renderFullExpense(response);
    document.getElementById("edit-expense").hidden = true;
    document.getElementById("start-edit").innerText = "Edit Expense"
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
    document.getElementById("name-header").innerText = `Welcome, ${userInfo.username.toLowerCase()}`;
    document.getElementById("new-expense-button").addEventListener("click", addExpense);
    document.getElementById("edit-expense-button").addEventListener("click", updateExpense);
    document.getElementById("edit-expense").dataset.userId = userInfo.userId;
    if(userInfo.isManager) {
        document.getElementById("all-expenses").hidden = false;
        document.getElementById("resolve-expense").addEventListener("click", resolveExpense);
        getMangerStats();
    }
    document.getElementById("start-edit").addEventListener("click", (e)=>{
        e.preventDefault();
        const isHidden = document.getElementById("edit-expense");
        isHidden.hidden = !isHidden.hidden;
        const toggle = document.getElementById("start-edit");
        toggle.innerText = isHidden.hidden ? "Edit Expense" : "Abandon Editing Expense";
    })
    const gridHolder = document.getElementsByClassName("grid-container")[0];
    const header = document.getElementsByClassName("Welcome")[0];
    gridHolder.style.gridTemplateRows = `${header.offsetHeight}px 0.8fr 0.5fr`;
}

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

/**Unfinished function */
async function getMangerStats() {
    if(!userInfo.isManager) {
        alert("You are not authorized to access Manager Statistics")
        return;
    }
    const httpRequest = await fetch(base+"/users/statistics", {
        method:"GET",
        headers:{
            "Content-type":"application/json",
            "Authorization":userInfo.jwt
        }
    });
    const stats = await httpRequest.json();
    calculateStats(stats);
}
/*
async function uploadFile(path) {
    let formData = new FormData();
    formData.append("file", path);
    const httpRequest = await fetch(base+"/users/upload", {method:"POST", headers:{"Authorization":userInfo.jwt},body:formData});
    const response = await httpRequest.text();
    console.log(response);
    return response;
}
*/
function calculateStats(stats) {
    const displayBox = document.getElementById("stats-display");
    displayBox.hidden=false;
    const displayList = document.getElementById("stats-summary");
    displayList.dataset.approvedCount = stats.approvedCount;
    displayList.dataset.deniedCount = stats.deniedCount;
    displayList.dataset.totalReimbursed = stats.totalReimbursed;
    if((stats.deniedCount + stats.approvedCount) === 0) {
        displayBox.innerHTML += `<h6>You haven't resolved any expenses</h6>`;
        return;
    }
    let display = `<li>Total Resolved Expenses: ${Number(stats.deniedCount) + Number(stats.approvedCount)}</li>`;
    display+= `<li>Total Approved: ${stats.approvedCount} (${formatPercent(Number(stats.approvedCount)/(Number(stats.approvedCount) + Number(stats.deniedCount)))})</li>`;
    display+= `<li>Total Denied: ${stats.deniedCount} (${formatPercent(Number(stats.deniedCount)/(Number(stats.approvedCount) + Number(stats.deniedCount)))})</li>`;
    display+= `<li>Total Reimbursed: ${formatAmount(Number(stats.totalReimbursed)/100)}</li>`;
    displayList.innerHTML = display;
}

function logout() {
    sessionStorage.removeItem("userInfo");
    window.location.assign("file:///C:/Users/Josh/IdeaProjects/BartProject1/frontend/page.html");
}
populatePage();