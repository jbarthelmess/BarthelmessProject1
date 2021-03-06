async function login() {
    let userInfo ={}; 
    const base = "http://35.202.96.201:7000";
    //const base = "http://localhost:7000";

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
        sessionStorage.setItem("userInfo",userInfo);
        const location = window.location.href;
        //console.log(location);
        const path = location.split("/");
        //console.log(path);
        path[path.length-1]="landing.html";
        const newPath = path.join("/");
        //console.log(newPath);
        window.location.assign(newPath);
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