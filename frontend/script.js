// ===================================================================
// This file is the "glue" between the HTML page and the Java backend.
// Every function that talks to the backend uses fetch(), the browser's
// built-in tool for making HTTP requests. The backend must be running
// on http://localhost:8080 for any of this to work.
// ===================================================================

const API_BASE = "http://localhost:8080/api";

// ---------- Tab switching ----------
document.querySelectorAll(".tab-btn").forEach((btn) => {
  btn.addEventListener("click", () => {
    document.querySelectorAll(".tab-btn").forEach((b) => b.classList.remove("active"));
    document.querySelectorAll(".tab-panel").forEach((p) => p.classList.remove("active"));
    btn.classList.add("active");
    document.getElementById(btn.dataset.tab).classList.add("active");

    if (btn.dataset.tab === "claims") {
      loadClaims();
      populatePolicyDropdown();
    }
  });
});

// ---------- Small helpers ----------
function showMessage(elementId, text, type) {
  const el = document.getElementById(elementId);
  el.textContent = text;
  el.className = "message " + type;
  setTimeout(() => { el.textContent = ""; el.className = "message"; }, 4000);
}

function formatDate(dateStr) {
  if (!dateStr) return "-";
  return new Date(dateStr).toLocaleDateString("en-IN", { day: "2-digit", month: "short", year: "numeric" });
}

function formatCurrency(amount) {
  return "₹" + Number(amount).toLocaleString("en-IN");
}

// ===================================================================
// POLICIES
// ===================================================================

async function loadPolicies(holderNameFilter = "") {
  try {
    const url = holderNameFilter
      ? `${API_BASE}/policies/search?holderName=${encodeURIComponent(holderNameFilter)}`
      : `${API_BASE}/policies`;

    const response = await fetch(url);
    if (!response.ok) throw new Error("Failed to load policies");
    const policies = await response.json();
    renderPolicyTable(policies);
  } catch (err) {
    showMessage("policyMessage", err.message, "error");
  }
}

function renderPolicyTable(policies) {
  const tbody = document.getElementById("policyTableBody");
  tbody.innerHTML = "";

  if (policies.length === 0) {
    tbody.innerHTML = `<tr><td colspan="7" style="text-align:center; color:#888;">No policies found</td></tr>`;
    return;
  }

  policies.forEach((policy) => {
    const isExpired = new Date(policy.expiryDate) < new Date();
    const row = document.createElement("tr");
    row.innerHTML = `
      <td>${policy.policyNumber}</td>
      <td>${policy.holderName}</td>
      <td>${policy.policyType}</td>
      <td>${formatCurrency(policy.premiumAmount)}</td>
      <td>${formatDate(policy.expiryDate)}</td>
      <td><span class="badge ${isExpired ? "expired" : "active"}">${isExpired ? "Expired" : "Active"}</span></td>
      <td>
        <button class="action-link danger" onclick="deletePolicy(${policy.id})">Delete</button>
      </td>
    `;
    tbody.appendChild(row);
  });
}

document.getElementById("policyForm").addEventListener("submit", async (e) => {
  e.preventDefault();

  const newPolicy = {
    policyNumber: document.getElementById("policyNumber").value,
    holderName: document.getElementById("holderName").value,
    holderEmail: document.getElementById("holderEmail").value,
    policyType: document.getElementById("policyType").value,
    premiumAmount: parseFloat(document.getElementById("premiumAmount").value),
    startDate: document.getElementById("startDate").value,
    expiryDate: document.getElementById("expiryDate").value,
  };

  try {
    const response = await fetch(`${API_BASE}/policies`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(newPolicy),
    });

    const data = await response.json();
    if (!response.ok) throw new Error(data.error || "Failed to create policy");

    showMessage("policyMessage", "Policy created successfully.", "success");
    e.target.reset();
    loadPolicies();
  } catch (err) {
    showMessage("policyMessage", err.message, "error");
  }
});

async function deletePolicy(id) {
  if (!confirm("Delete this policy? This will also delete its claims.")) return;
  try {
    const response = await fetch(`${API_BASE}/policies/${id}`, { method: "DELETE" });
    if (!response.ok) throw new Error("Failed to delete policy");
    loadPolicies();
  } catch (err) {
    showMessage("policyMessage", err.message, "error");
  }
}

let searchDebounce;
document.getElementById("searchInput").addEventListener("input", (e) => {
  clearTimeout(searchDebounce);
  searchDebounce = setTimeout(() => loadPolicies(e.target.value), 300);
});

// ===================================================================
// CLAIMS
// ===================================================================

async function populatePolicyDropdown() {
  try {
    const response = await fetch(`${API_BASE}/policies`);
    const policies = await response.json();
    const select = document.getElementById("claimPolicySelect");
    select.innerHTML = `<option value="">Select Policy</option>`;
    policies.forEach((p) => {
      select.innerHTML += `<option value="${p.id}">${p.policyNumber} - ${p.holderName}</option>`;
    });
  } catch (err) {
    console.error("Could not load policies for dropdown", err);
  }
}

async function loadClaims() {
  try {
    const response = await fetch(`${API_BASE}/claims`);
    if (!response.ok) throw new Error("Failed to load claims");
    const claims = await response.json();
    renderClaimTable(claims);
  } catch (err) {
    showMessage("claimMessage", err.message, "error");
  }
}

function renderClaimTable(claims) {
  const tbody = document.getElementById("claimTableBody");
  tbody.innerHTML = "";

  if (claims.length === 0) {
    tbody.innerHTML = `<tr><td colspan="7" style="text-align:center; color:#888;">No claims filed yet</td></tr>`;
    return;
  }

  claims.forEach((claim) => {
    const row = document.createElement("tr");
    row.innerHTML = `
      <td>${claim.claimNumber}</td>
      <td>${claim.policy ? claim.policy.policyNumber : "-"}</td>
      <td>${claim.description}</td>
      <td>${formatCurrency(claim.claimAmount)}</td>
      <td>${formatDate(claim.filedDate)}</td>
      <td><span class="badge ${claim.status.toLowerCase()}">${claim.status}</span></td>
      <td>
        <select class="status-select" onchange="updateClaimStatus(${claim.id}, this.value)">
          <option value="">Change status</option>
          <option value="PENDING">Pending</option>
          <option value="APPROVED">Approved</option>
          <option value="REJECTED">Rejected</option>
        </select>
        <button class="action-link danger" onclick="deleteClaim(${claim.id})">Delete</button>
      </td>
    `;
    tbody.appendChild(row);
  });
}

document.getElementById("claimForm").addEventListener("submit", async (e) => {
  e.preventDefault();

  const policyId = document.getElementById("claimPolicySelect").value;
  if (!policyId) {
    showMessage("claimMessage", "Please select a policy.", "error");
    return;
  }

  const newClaim = {
    description: document.getElementById("claimDescription").value,
    claimAmount: parseFloat(document.getElementById("claimAmount").value),
  };

  try {
    const response = await fetch(`${API_BASE}/policies/${policyId}/claims`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(newClaim),
    });

    const data = await response.json();
    if (!response.ok) throw new Error(data.error || "Failed to file claim");

    showMessage("claimMessage", "Claim filed successfully.", "success");
    e.target.reset();
    loadClaims();
  } catch (err) {
    showMessage("claimMessage", err.message, "error");
  }
});

async function updateClaimStatus(claimId, newStatus) {
  if (!newStatus) return;
  try {
    const response = await fetch(`${API_BASE}/claims/${claimId}/status?status=${newStatus}`, {
      method: "PUT",
    });
    if (!response.ok) throw new Error("Failed to update claim status");
    loadClaims();
  } catch (err) {
    showMessage("claimMessage", err.message, "error");
  }
}

async function deleteClaim(id) {
  if (!confirm("Delete this claim?")) return;
  try {
    const response = await fetch(`${API_BASE}/claims/${id}`, { method: "DELETE" });
    if (!response.ok) throw new Error("Failed to delete claim");
    loadClaims();
  } catch (err) {
    showMessage("claimMessage", err.message, "error");
  }
}

// ---------- Initial load ----------
loadPolicies();
