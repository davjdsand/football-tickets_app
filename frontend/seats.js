// handles the stadium and seat selection logic

console.log("✅ seats.js is loaded and running!");
// possible cuurent states
let currentTransaction = {
    match_id: null,
    zone_name: null, 
    price: 0,
    seat_nr: null
};


// open the flow
// both seats and matches.js are in teh same html so can share datas
function buyTicket (match_id) {
    const match = allMatches.find(m => m.id === match_id);

    if (!match) {
        console.error("match not found: id = ", match_id);
        return;
    }

    // save it
    currentTransaction.match_id = match_id;

    // Reset previous selections
    currentTransaction.zone_name = null;
    currentTransaction.seatNumber = null;

    //  show stadium view first
    document.getElementById('stadium-view').style.display = 'flex';
    document.getElementById('seat-view').style.display = 'none';
    
    // set Title
    document.getElementById('ticketModalTitle').innerText = "Select Area - " + match.teamHome + " vs " + match.teamAway;

    // show Modal
    document.getElementById('ticketModal').style.display = 'block';
    document.getElementById('ticketModalOverlay').style.display = 'block';
}

// select zone
function selectZone (zone_name, base_price_modifier) {
    const match = allMatches.find(m => m.id === currentTransaction.match_id);
    const base_price = match.price;

    currentTransaction.zone_name = zone_name;
    currentTransaction.price = base_price_modifier;

    document.getElementById('stadium-view').style.display = 'none';
    document.getElementById('seat-view').style.display = 'block';
    
    document.getElementById('selected-zone-display').innerText = `Zone: ${zone_name} - $${currentTransaction.price}`;
    document.getElementById('ticketModalTitle').innerText = "Select Your Seat";

    generateSeats(); // Draw the grid
}


// genearte seats
function generateSeats() {
    const grid = document.getElementById('seats-grid');
    grid.innerHTML = ""; // Clear old seats

    // Create 40 mock seats
    for (let i = 1; i <= 40; i++) {
        const seat_div = document.createElement('div');
        seat_div.className = 'seat';
        seat_div.innerText = i;
        
        // Randomly make some seats "Taken" (Red)
        if (Math.random() < 0.2) { 
            seat_div.classList.add('taken');
        } else {
            // Make available seats clickable
            seat_div.onclick = function() {
                // Visual: Highlight this seat, unhighlight others
                document.querySelectorAll('.seat').forEach(s => s.classList.remove('selected'));
                seat_div.classList.add('selected');
                
                // Logic: Save selection
                currentTransaction.seat_number = i;
                
                // UI: Enable the confirm button
                const btn = document.getElementById('confirm-seat-btn');
                btn.disabled = false;
                btn.innerText = `Buy Seat ${i} for $${current_transaction.price}`;
            };
        }
        grid.appendChild(seat_div);
    }
}



// navigation buttnos
function backToStadium() {
    document.getElementById('seat-view').style.display = 'none';
    document.getElementById('stadium-view').style.display = 'flex';
    document.getElementById('ticketModalTitle').innerText = "Select Area";
}

function closeTicketModal() {
    document.getElementById('ticketModal').style.display = 'none';
    document.getElementById('ticketModalOverlay').style.display = 'none';
}

// fnal confirmation
function confirmPurchase() {
    if(!currentTransaction.seat_number) return;

    alert(`SUCCESS!\n\nYou bought a ticket for:\nZone: ${current_transaction.zone_name}\nSeat: ${current_transaction.seat_number}\nPrice: $${current_transaction.price}`);
    
    closeTicketModal();
}

















