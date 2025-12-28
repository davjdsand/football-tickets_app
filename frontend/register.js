function doRegister() {

    const u = document.getElementById("reg-username").value;
    const p = document.getElementById("reg-password").value;
    const msg = document.getElementById("reg-message");

    // this connect with java Login()
    fetch('http://localhost:8080/api/register', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username: u, password: p })
    })
        .then(response => {
            if (response.status === 200) {
                alert("Account created successfully! Please login.");
                window.location.href = "index.html"; // back to login   
            } else if (response.status === 409) {
                msg.style.color = "red";
                msg.innerText = "Username already exists";    
            } else {
                msg.style.color = "red";
                msg.innerText = "Registration failed";
            }
        })

        .catch(err => {
            console.error(err);
            msg.style.color = "red";
            msg.innerText = "Server error";
      });
}