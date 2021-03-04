async function login() {
    // get login info from inputs
    let loginPackage = {};
    loginPackage.username = document.getElementById("username").value;
    console.log(loginPackage.username);
    if(loginPackage.username === null) {
        alert("username cannot be null");
        return;
    }
    loginPackage.password = document.getElementById("password").value;
    console.log(loginPackage.password);
    if(loginPackage.username === null) {
        alert("password cannot be null");
        return;
    }

    // send login request
    const response = await fetch("http://localhost:7000/users/login",{
        method:"POST",
        headers:{
            "Content-Type":"application/javascript"
        },
        body:JSON.stringify(loginPackage)
    });
    const JWT = await response.text();
    console.log(JWT);
}

document.addEventListener("keypress", function(e) {
    if(e.key === 'Enter') {
        login();
    }
});