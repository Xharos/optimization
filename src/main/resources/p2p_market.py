#Peer to peer electricity market

import matplotlib.pyplot as plt
import numpy as np

N           = 20                            # number of countries
A           = np.random.rand(N, 1)          # vector of random values in [0;1[
P0          = np.random.rand(N, 1) - 0.5    # cost, P0_i > 0 equal a producer, or else a consummer
m_iter      = 200                           # max problem iteration
mp_iter     = 100                           # max partage problem iteration
rho         = 0.1                           # augmented lagrangian parameter
rhop        = 0.1                           # augmented partage lagrangian parameter

T   = np.zeros((N, N, m_iter))  # skew-symmetric matrix of trades for m_iter iterations
L   = np.zeros((N, N, m_iter))  # penalty parameter of augmented Lagrangian
P   = np.zeros((N, m_iter))     # power calculated
PM  = np.zeros((1, m_iter))     # dual variables

#return all trades for agent n (T_{nm})_n
def partage(a, p0, l, C):
    pm  = np.zeros((1, mp_iter))
    t   = np.zeros((N, mp_iter))
    tm  = np.zeros((1, mp_iter))
    mu  = np.zeros((1, mp_iter))
    for k in range(mp_iter - 1):
        for m in range(N):
            tmp_a       = C[m] - l[m]/rho
            tmp_b       = t[m, k] - tm[0, k] + pm[0, k] - mu[0, k]/rhop
            t[m, k+1]   = (rho*tmp_a + rhop*tmp_b)/(rho + rhop)
        tm[0, k+1]  = np.mean(t[:, k+1])
        pm[0, k+1]  = (2*a*p0 + mu[0, k] + rhop*tm[0, k+1])/(2*a*N + rhop)
        mu[0, k+1]  = mu[0, k] + rhop*(tm[0, k+1] - pm[0, k+1])
    return t[:, -1]


for k in range(m_iter - 1):  # iteration for convergence
    for n in range(N):      # iteration for each agent
        C               = (T[n, :, k] - T[:, n, k].T)/2
        T[n, :, k+1]    = partage(A[n][0], P0[n][0], L[n, :, k], C)
        P[n, k+1]       = sum(T[n, :, k+1])
    PM[0, k+1]      = sum(sum(T[:, :, k+1]))
    L[:, :, k+1]    = L[:, :, k] + rho*(T[:, :, k+1] + T[:, :, k+1].T)

t = np.arange(0, m_iter, 1)
plt.figure()
for n in range(N):
    plt.plot(t, P[n, :])
plt.title("P2P market resolution")
plt.xlabel("iterations")
plt.ylabel("Power (MW)")
plt.show()