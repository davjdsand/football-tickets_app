
// registartion/login check
// const stored_user = sessionStorage.getItem('current_user');

// if (!stored_user) {
//     alert("Please login first!");
//     window.location.href = "index.html"; // back to login
// }

// const user = JSON.parse(stored_user);
// document.getElementById("welcome_user").innerText = "Welcome" + user.username;


//logout logic
function doLogout() {
    sessionStorage.removeItem('current_user');
    window.location.href = "index.html";
}

//

fetch('http://localhost:8080/api/matches')     
    .then(response => response.json())
    .then(matches => {
        const container = document.getElementById("matches_container");

        // loop through every match java sent 

        matches.forEach(match => {
            const card = document.createElement('div');
            card.className = 'match-card';

            // I removed the comment from inside the string below
            card.innerHTML = `
                <img src="${match.image_url}" onerror="this.onerror=null; this.src='https://via.placeholder.com/300'">
                <div class="card-details">
                    <h3>${match.teamHome} vs ${match.teamAway}</h3>
                    <p>📅 ${match.matchDate}</p>
                    <p>📍 ${match.location}</p>
                    <div class="price-tag">$${match.price}</div>
                    <button onclick="buyTicket(${match.id})">Buy Ticket</button>
                </div>
            `;

            // Add it to the page
            container.appendChild(card);    
        });


    })
.catch(err => console.error("Error loading matches:", err));

function buyTicket(matchId) {
    alert("You clicked buy for Match ID: " + matchId);
    // Later we can add real booking logic here
}


function toggleMenu() {
    const menu = document.getElementById("dropdown-menu");
    menu.classList.toggle("show");
}


// close the menu if user clicks somewhere else on the screen

window.onclick = function(event) {
    if (!event.target.matches('.burger-icon')) {
        const dropdowns = document.getElementsByClassName("dropdown-content");
        for (let i  = 0; i < dropdowns.length; i++) {
            if (dropdowns[i].classList.contains('show')) {
                dropdowns[i].classList.remove('show');
            }
        }
    }
}








