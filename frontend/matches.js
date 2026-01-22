// Check if user is logged in
const stored_user_json = sessionStorage.getItem('current_user');
if (!stored_user_json) {
    alert("Please login first!");
    window.location.href = "index.html"; 
}

const currentUser = JSON.parse(stored_user_json);
document.getElementById("welcome_user").innerText = "Welcome, " + currentUser.username;

// Logout logic
function doLogout() {
    sessionStorage.removeItem('current_user');
    window.location.href = "index.html";
}

// Admin Check
const is_admin = currentUser && (currentUser.role === 'ADMIN' || currentUser.role === 'admin');

if (is_admin) {
    const container = document.getElementById("matches_container");
    if (container) {
        const add_btn = document.createElement("button");
        add_btn.innerText = "➕ Add New Match";
        add_btn.className = "add-match-button";
        add_btn.onclick = openAddMatchModal;
        container.parentNode.insertBefore(add_btn, container);
    }
}

// Store matches locally
let allMatches = [];

// Fetch Matches from Backend
fetch('http://localhost:8080/api/matches')
    .then(response => response.json())
    .then(matches => {
        allMatches = matches;
        const container = document.getElementById("matches_container");

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
                    <div class="admin-controls">
                        <button onclick="buyTicket(${match.id})">Buy Ticket</button>
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
            container.appendChild(card);
        });
    })
    .catch(err => console.error("Error loading matches:", err));

// Navbar Menu Logic
function toggleMenu() {
    const menu = document.getElementById("dropdown-menu");
    menu.classList.toggle("show");
}

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

// --- ADMIN FUNCTIONS ---

function deleteMatch(id) {
    if (!confirm("Are you sure you want to delete this match?")) return;
    
    fetch(`http://localhost:8080/api/matches/${id}`, { method: 'DELETE' })
        .then(response => {
            if (response.ok) {
                alert("Match deleted!");
                location.reload();
            } else {
                alert("Failed to delete");
            }
        });
}

function editMatch(id) {
    const match = allMatches.find(m => m.id === id);
    if (!match) return;

    document.getElementById("edit-id").value = match.id;
    document.getElementById("edit-home").value = match.teamHome;
    document.getElementById("edit-away").value = match.teamAway;
    document.getElementById("edit-stadium").value = match.stadium; 
    document.getElementById("edit-date").value = match.matchDate;
    document.getElementById("edit-location").value = match.location;
    document.getElementById("edit-price").value = match.price;
    document.getElementById("edit-image").value = match.image_url || "";

    document.getElementById('editModal').style.display = 'block';
    document.getElementById('editModalOverlay').style.display = 'block';
}

function saveMatchChanges() {
    const updateMatch = {
        id: document.getElementById('edit-id').value,
        teamHome: document.getElementById('edit-home').value,
        teamAway: document.getElementById('edit-away').value,
        stadium: document.getElementById('edit-stadium').value, 
        matchDate: document.getElementById('edit-date').value,
        location: document.getElementById('edit-location').value,
        price: document.getElementById('edit-price').value,
        image_url: document.getElementById('edit-image').value
    };

    fetch('http://localhost:8080/api/matches', {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(updateMatch)
    }).then(response => {
        if (response.ok) {
            alert("Match Updated!");
            location.reload();
        } else {
            alert("Error updating match");
        }
    });
}

function closeEditModal() {
    document.getElementById('editModal').style.display = 'none';
    document.getElementById('editModalOverlay').style.display = 'none';
}

function openAddMatchModal() {
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
        alert("Please fill at least teams and price");
        return;
    }

    fetch('http://localhost:8080/api/matches', {
        method: 'POST',
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

function closeAddModal() {
    const modal = document.getElementById('addModal');
    if (modal) modal.style.display = 'none';
    const overlay = document.getElementById('addModalOverlay');
    if (overlay) overlay.style.display = 'none';
}

// --- ORDER HISTORY (WALLET) ---

function showOrderHistory() {
    const storedUser = sessionStorage.getItem('current_user');
    if (!storedUser) {
        alert("Please login to view your wallet.");
        return;
    }
    const user = JSON.parse(storedUser);

    // Get Modal Elements
    const modal = document.getElementById("historyModal");
    const list = document.getElementById("historyList");

    // Open Modal
    modal.style.display = "flex";
    list.innerHTML = "<p>Loading your wallet...</p>";

    // Fetch from Backend (Correctly Encoding Username)
    fetch(`http://localhost:8080/api/history?username=${encodeURIComponent(user.username)}`)
        .then(response => {
            if (!response.ok) throw new Error("Failed to fetch history");
            return response.json();
        })
        .then(data => {
            list.innerHTML = ""; // Clear loading text

            if (data.length === 0) {
                list.innerHTML = "<p style='text-align:center'>Your wallet is empty.</p>";
                return;
            }

            // Render Tickets
            data.forEach(ticket => {
                const item = document.createElement("div");
                item.className = "ticket-item"; // Uses the new CSS class
                
                item.innerHTML = `
                    <div class="ticket-header">⚽ ${ticket.match}</div>
                    <div class="ticket-info">
                        📅 <b>Date:</b> ${ticket.date} <br>
                        📍 <b>Stadium:</b> ${ticket.stadium} <br>
                        💺 <b>Seat:</b> ${ticket.zone} - ${ticket.seat} 
                        <span style="float:right; color:green; font-weight:bold;">$${ticket.price}</span>
                    </div>
                `;
                list.appendChild(item);
            });
        })
        .catch(err => {
            console.error(err);
            list.innerHTML = "<p style='color:red; text-align:center;'>Error loading wallet.</p>";
        });
}