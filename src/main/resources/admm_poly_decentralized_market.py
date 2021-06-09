#!/usr/bin/python
import matplotlib.pyplot as plt
import numpy as np
import random
import time
import ast
import sys

N           = (len(sys.argv) - 2)*2     # number of agents
DATA        = "../../../data/"                   # directory
date        = sys.argv[1]               # the date to load data from
P0          = []                        # consummers power
A           = []                        # consummers power request polynom
coe         = 10                        # a*coe(P-P0)^2
producerp   = []                        # producer[countryId] return a list of poly1D coefficients
producer    = []
lineCap     = [[0]*int(N/2) for i in range(int(N/2))]

with open(DATA + date + ".txt", 'r') as reader:
    for line in reader.readlines():
        P0.append(float(line.replace(',', '.')))
        A.append(random.randint(1, 9)*coe)

with open(DATA + "line.txt", 'r') as reader:
    i = 0
    for line in reader.readlines():
        parts = line.split("\t")
        for j in range(len(parts)):
            lineCap[i][j] = int(parts[j])
        i += 1

for i in range(int(N/2)):
    for j in range(i+1, int(N/2)):
        lineCap[j][i] = lineCap[i][j]

for j in range(int(N/2)):
    print(lineCap[j])

def getProducer(id):
    producerp.append([])
    with open(DATA + id + "-poly.txt", 'r') as reader:
        line = reader.readlines()
        producerp[-1] = ast.literal_eval(line[0])
    producer.append([[], []])
    with open(DATA + id + "-inter.txt", 'r') as reader:
        line = reader.readlines()
        producer[-1][0] = ast.literal_eval(line[0])
        producer[-1][1] = ast.literal_eval(line[1])

for i in range(2, len(sys.argv)):
    getProducer(sys.argv[i])

m_iter      = 400       # max problem iteration
mp_iter     = 20        # max partage problem iteration

def admm(rho, rhop):

    T   = np.zeros((N, N, m_iter))  # skew-symmetric matrix of trades for m_iter iterations
    L   = np.zeros((N, N, m_iter))  # penalty parameter of augmented Lagrangian
    P   = np.zeros((N, m_iter))     # power calculated
    PM  = np.zeros((1, m_iter))     # dual variables

    def partage(l, C, n):
        pm  = np.zeros((1, mp_iter))
        t   = np.zeros((N, mp_iter))
        tm  = np.zeros((1, mp_iter))
        mu  = np.zeros((1, mp_iter))
        for k in range(mp_iter - 1):
            for m in range(N):
                tmp_a       = C[m] - l[m]/rho
                tmp_b       = t[m, k] - tm[0, k] + pm[0, k] - mu[0, k]/rhop
                t[m, k+1]   = (rho*tmp_a + rhop*tmp_b)/(rho + rhop)
                if(abs(n-m) == int(N/2)):
                    if(t[m, k+1] >= max(producer[min(n, m)][0])):
                        t[m, k+1] = max(producer[min(n, m)][0])
                if(t[m, k+1] >= lineCap[n % int(N/2)][m % int(N/2)]):
                    t[m, k+1] = lineCap[n % int(N/2)][m % int(N/2)]
                if(t[m, k+1] <= -lineCap[n % int(N/2)][m % int(N/2)]):
                    t[m, k+1] = -lineCap[n % int(N/2)][m % int(N/2)]
            tm[0, k+1]  = np.mean(t[:, k+1])
            if(n < int(N/2)):
                a           = A[n]
                p0          = -P0[n]
                pm[0, k+1]  = (2*a*p0 + mu[0, k] + rhop*tm[0, k+1])/(2*a*N + rhop)
            else:
                i   = n - int(N/2)
                a   = producerp[i][0]
                b   = producerp[i][1]
                pm[0, k+1]  = (mu[0, k] + rhop*tm[0, k+1] - b)/(2*a*N + rhop)
                if(pm[0, k+1]*N > max(producer[i][0])):
                    pm[0, k+1] = max(producer[i][0])/N
                if (pm[0, k+1]*N < min(producer[i][0])):
                    pm[0, k+1] = 0
            mu[0, k+1]  = mu[0, k] + rhop*(tm[0, k+1] - pm[0, k+1])
        return t[:, -1]

    for k in range(m_iter - 1):     # iteration for convergence
        for n in range(N):          # iteration for each agent
            C               = (T[n, :, k] - T[:, n, k].T)/2
            T[n, :, k+1]    = partage(L[n, :, k], C, n)
            P[n, k+1]       = sum(T[n, :, k+1])
        PM[0, k+1]      = sum(sum(T[:, :, k+1]))
        L[:, :, k+1]    = L[:, :, k] + rho*(T[:, :, k+1] + T[:, :, k+1].T)
        if(k > 5):
            cvg = [abs(P[int(N/2) + i, k+1]) - abs(P[int(N/2) + i, k]) for i in range(int(N/2))]
            if(all(abs(e) < 1e-4 for e in cvg)):
                if(sum(P[:, -1]) < 0.1):
                    return (P[:, 0:k+1], T[:, :, k+1])
    return (P, T[:, :, -1])

ro  = []
ti  = []
sup = []

start = time.time()
(P, T) = admm(0.1, 0.1)
for e in P[int(N/2):, -1]:
    print(e)
end = time.time()
ro.append(i)
ti.append(end-start)
sup.append(sum(P[:, -1]))
print("New test for rho = rho' =", 0.1)
print("Result is =", P[:, -1])
print("Sum Pn =", sup[-1])
print("Executed in", end-start)
print("")
np.set_printoptions(threshold=np.inf)
matri = np.array2string(T[0:int(N/2), int(N/2):], precision=1, separator=',', suppress_small=True).replace('\n', '').split(']')
for i in range(len(matri)):
    print(matri[i].replace('\n', ''))
#print(np.array2string(T[int(N/2):,int(N/2):], precision=2, separator=',', suppress_small=True).replace('\n', ''))
for i in range(int(N/2), N):
    for j in range(i+1, N):
        if(lineCap[i - int(N/2)][j - int(N/2)] > 0 and T[i, j] == lineCap[i - int(N/2)][j - int(N/2)]):
            print("warn")

ma = -max(P0)
mi = -min(P0)
ymax = 0
xmax = 0
t = np.linspace(int(ma + ma/8), int(mi - ma/8), 10000)
plt.figure()
plt.subplot(211)
for n in range(int(N/2)):
    plt.plot(t, [A[n]*((P + P0[n])**2) for P in t], '--', label=sys.argv[2 + n])

for n in range(int(N/2)):
    nymax = max(producer[n][1])
    nxmax = max(producer[n][0])
    if (nymax > ymax):
        ymax = nymax
    if (nxmax > xmax):
        xmax = nxmax
    plt.plot(producer[n][0], producer[n][1], label=sys.argv[2 + n])

t = np.linspace(0, xmax, 100)
for n in range(int(N/2)):
    poly = np.poly1d([producerp[n][0], producerp[n][1], producerp[n][2]])#producerp[n][1], producerp[n][2]])
    plt.plot(t, [np.polyval(poly, t[i]) for i in range (len(t))], label="Poly2 for " + str(sys.argv[2 + n]))
for i in range(len(P[:, -1])):
    if (i < int(N/2)):
        p0 = -P0[i]
        plt.plot(P[:, -1][i], A[i]*((P[:, -1][i] - p0)**2), 'x')
    else:
        j = i - int(N/2)
        poly = np.poly1d([producerp[j][0], producerp[j][1], producerp[j][2]])
        plt.plot(P[:, -1][i], np.polyval(poly, P[:, -1][i]), 'o')
plt.legend()
plt.xlabel("Power (MW)")
plt.ylabel("Cost (€)")
plt.title("Lincost for market")
plt.xlim([int(ma + ma/8), int(xmax + xmax/4)])
plt.ylim([0, 1.1*ymax])

t = np.arange(0, P.shape[1] , 1)

plt.subplot(212)
for n in range(N):
    plt.plot(t, P[n, :], label=("Consumer in "+str(sys.argv[2 + n]) if n < int(N/2) else "producer in "+str(sys.argv[2 + n - int(N/2)])))
plt.title("P2P market resolution")
plt.xlabel("iterations")
plt.legend()
plt.ylabel("Power (MW)")
plt.show()