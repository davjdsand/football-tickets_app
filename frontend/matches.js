
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

const stored_user = JSON.parse(sessionStorage.getItem('current_user'));
const is_admin = stored_user && stored_user.role === 'ADMIN';

if (is_admin) {
    const container = document.getElementById("matches_container");
    if (container) {
        const add_btn = document.createElement("button");
        add_btn.innerText = "➕ Add New Match";
        add_btn.className = "add-match-button";
        add_btn.onclick = openAddMatchModal;

        container.parentNode.insertBefore(add_btn, container);
    } else {
        console.error("Couldnt find match_container in html");
    }
    
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
            let action_btn = '';

            
            if (is_admin) {
                action_btn = `
                    <div class="admin-controls">
                        <button onclick="editMatch(${match.id})" class="btn-edit">✏️ Edit</button>
                        <button onclick="deleteMatch(${match.id})" class="btn-delete">🚮 Delete</button>
                    </div>
                `;
            } else {
                action_btn = `
                    <div class = "admin-controls">
                    <button onclick = "buyTicket(${match.id})">Buy Ticket</button>
                    </div>
                `;
            }


            card.innerHTML = `
                <img src="${match.image_url}" onerror="this.onerror=null; this.src='https://via.placeholder.com/300'">
                <div class="card-details">
                    <h3>${match.teamHome} vs ${match.teamAway}</h3>
                    <p>📅 ${match.matchDate}</p>
                    <p>📍 ${match.location}</p>
                    <div class="price-tag">$${match.price}</div>
                    ${action_btn}
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

window.onclick = function (event) {
    if (!event.target.matches('.burger-icon')) {
        const dropdowns = document.getElementsByClassName("dropdown-content");
        for (let i = 0; i < dropdowns.length; i++) {
            if (dropdowns[i].classList.contains('show')) {
                dropdowns[i].classList.remove('show');
            }
        }
    }
}


// admin functions
function deleteMatch(id) {
    if (!confirm("Are u sure you want to delete this match?")) {
        return;
    }
    // send delete request to backend
    fetch(`http://localhost:8080/api/matches/${id}`, {
        method: 'DELETE'
    })
        .then(response => {
            if (response.ok) {
                alert("Match deleted!");
                location.reload();// automaticly refresh the page
            } else {
                alert("Failed to delete");
            }
        });
}


function editMatch(id) {
    let new_price = prompt("Enter new price: ");
    if (new_price) {
        console.log(`Updating match ${id} with price ${new_price}`);
    }
}


function openAddMatchModal() {
    alert("Open a form  to ad a match here!");
}



