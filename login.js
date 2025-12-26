function doLogin() {

    const u = document.getElementById("username").value;
    const p = document.getElementById("password").value;

    // this connect with java Login()
    fetch('http://localhost:8080/api/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username: u, password: p })
    })
        .then(response => {
            if (response.status === 200) {
                return response.json();
            } else {
                throw new Error("Login failed");
            }
        })

        .then(userData => {

            // login succesful
            document.getElementById("message").style.color = "green";
            document.getElementById("message").innerText = "Hello " + userData.username;

            if (userData.role === "ADMIN") {
                alert("REDIRECTING TO ADMIN PANEL...");
            }


            // save user infos then redirect where matches are
            sessionStorage.setItem('current_user', JSON.stringify(userData));
            window.location.href = "matches.html";

        })

        .catch(err => {
            document.getElementById("message").style.color = "red";
            document.getElementById("message").innerText = "Wrong username or password";
      });
}