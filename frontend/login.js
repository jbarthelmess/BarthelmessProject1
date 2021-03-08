async function login() {
    let userInfo ={}; 
    const base = "http://localhost:7000";

    // get login info from inputs
    let loginPackage = {};
    loginPackage.username = document.getElementById("username").value;
    if(loginPackage.username === null) {
        alert("username cannot be null");
        return;
    }
    loginPackage.password = document.getElementById("password").value;
    if(loginPackage.username === null) {
        alert("password cannot be null");
        return;
    }

    // send login request
    const response = await fetch(base+"/users/login",{
        method:"POST",
        headers:{
            "Content-Type":"application/javascript"
        },
        body:JSON.stringify(loginPackage)
    });
    userInfo = await response.text();
    try {
        const userData = JSON.parse(userInfo);
        localStorage.setItem("userInfo",userInfo);
        window.location.href = "file:///C:/Users/Josh/IdeaProjects/BartProject1/frontend/landing.html"
    } catch(e) {
        alert("Login Attempt failed, please try again");
        return;
    }
}

document.addEventListener("keypress", function(e) {
    if(e.key === 'Enter') {
        login();
    }
});