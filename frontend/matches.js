
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


// store matches locally
let allMatches = [];


fetch('http://localhost:8080/api/matches')
    .then(response => response.json())
    .then(matches => {
        allMatches = matches;
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

function buyTicket(id) {
    const match = allMatches.find(m => m.id == id)
    if (match) {
        alert(`You are buying a ticket for: ${match.teamHome} vs ${match.teamAway}`);
    } else {
        alert("Match not found");
    }
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
    // find match datas by ids
    const match = allMatches.find(m => m.id === id);
    if (!match) {
        return;
    }

    // fill inputs
    document.getElementById("edit-id").value = match.id;
    document.getElementById("edit-home").value = match.teamHome;
    document.getElementById("edit-away").value = match.teamAway;
    document.getElementById("edit-date").value = match.matchDate;
    document.getElementById("edit-location").value = match.location;
    document.getElementById("edit-price").value = match.price;

    // show modal
    document.getElementById('editModal').style.display = 'block';
    document.getElementById('editModalOverlay').style.display = 'block';

}

function saveMatchChanges() {
    const id = document.getElementById('edit-id').value;
    const home = document.getElementById('edit-home').value;
    const away = document.getElementById('edit-away').value;
    const date = document.getElementById('edit-date').value;
    const loc = document.getElementById('edit-location').value;
    const price = document.getElementById('edit-price').value;

    const updateMatch = {
        id: id,
        teamHome: home,
        teamAway: away,
        matchDate: date,
        location: loc,
        price: price
    };

    fetch('http://localhost:8080/api/matches', {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(updateMatch)
    }).then(response => {
        if (response.ok) {
            alert("Match Updated!");
            location.reload(); // Refresh to see changes
        } else {
            alert("Error updating match");
        }
    });
}

function closeEditModal () {
    document.getElementById('editModal').style.display = 'none';
    document.getElementById('editModalOverlay').style.display = 'none';
}


function openAddMatchModal() {
    // clear inputs
    document.getElementById('add-home').value = '';
    document.getElementById('add-away').value = '';
    document.getElementById('add-stadium').value = '';
    document.getElementById('add-date').value = '';
    document.getElementById('add-location').value = '';
    document.getElementById('add-price').value = '';
    document.getElementById('add-image').value = '';

    document.getElementById('addModal').style.display = 'block';
    document.getElementById('addModalOverlay').style.display = 'block';
}

function saveNewMatch() {
    const new_match = {
        teamHome: document.getElementById('add-home').value,
        teamAway: document.getElementById('add-away').value,
        stadium: document.getElementById('add-stadium').value,
        matchDate: document.getElementById('add-date').value,
        location: document.getElementById('add-location').value,
        price: document.getElementById('add-price').value,
        image_url: document.getElementById('add-image').value
    };

    if (!new_match.teamHome || !new_match.teamAway || !new_match.price) {
        alert("Please fill i at least teams and price");
        return;
    }

    fetch('http://localhost:8080/api/matches', {
        method: 'POST', // POST means "Create New"
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(new_match)
    })
    .then(response => {
        if (response.ok) {
            alert("Match Created Successfully!");
            location.reload(); 
        } else {
            alert("Error creating match");
        }
    });
}


