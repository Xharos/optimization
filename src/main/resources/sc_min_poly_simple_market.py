#!/usr/bin/python
from scipy.optimize import minimize, LinearConstraint
#import matplotlib.pyplot as plt
import numpy as np
import random
import ast
import sys

DATA = "data/"
date = sys.argv[1]
p = []
a = []
coe = 1
consumer = []
producer = []
producerp = []

with open(DATA + date + ".txt", 'r') as reader:
    for line in reader.readlines():
        p.append(float(line.replace(',', '.')))

ma = -max(p)
mi = -min(p)
t = np.linspace(int(ma + ma/8), int(mi - ma/8), 10000)

for i in range(len(p)):
    an = random.randint(1, 9)
    P0 = -p[i]
    a.append(an*coe)
    consumer.append([an*coe*((P - P0)**2) for P in t])

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


#plt.figure()

#for i in range(len(consumer)):
#    plt.plot(t, consumer[i], '--')

#ymax = 0
#xmax = 0
#for i in range(len(producer)):
#    nymax = max(producer[i][1])
#    nxmax = max(producer[i][0])
#    t = np.linspace(0, nxmax, 100)
#    if (nymax > ymax):
#        ymax = nymax
#    if (nxmax > xmax):
#        xmax = nxmax
#    poly = np.poly1d([producerp[i][0], producerp[i][1], producerp[i][2]])
#    plt.plot(t, [np.polyval(poly, t[n]) for n in range (len(t))])

#plt.xlabel("Puissance (MW)")
#plt.ylabel("Coût (€)")
#plt.title("Graph de minimisation du marché en date du : " + date)
#plt.xlim([int(ma + ma/4), int(xmax + xmax/4)])
#plt.ylim([0, 1.1*ymax])

def sum_fn(P):
    result = []
    for i in range(len(consumer)):
        P0 = -p[i]
        result.append(a[i]*((P[i] - P0)**2))
    for i in range(len(producer)):
        poly = np.poly1d([producerp[i][0], producerp[i][1], producerp[i][2]])
        result.append(np.polyval(poly, P[i+ len(consumer)]))
    return sum(result)

constraint = LinearConstraint(np.ones(len(consumer) + len(producer)), lb=0, ub=0) #sum of Pn equals 0, from doc lb <= A.dot(x) <= ub, ca ca marche
bounds = [(int(ma + ma/4), 0) for n in range(len(consumer))] + [(0, max(producer[n][0])) for n in range(len(producer))]
res = minimize(sum_fn, 100*np.ones(len(consumer) + len(producer)), method='SLSQP', constraints=constraint, bounds=bounds)
#print(res)
#for i in range(len(res.x)):
#    if (i < len(consumer)):
#        P0 = -p[i]
#        plt.plot(res.x[i], a[i]*((res.x[i] - P0)**2), 'o')
#    else:
#        j = i - len(consumer)
#        poly = np.poly1d([producerp[j][0], producerp[j][1], producerp[j][2]])
#        plt.plot(res.x[i], np.polyval(poly, res.x[i]), 'o')
#print("Sum Pn =", sum(res.x))
#plt.show()

for i in range(0, len(producer)):
    print(res.x[len(consumer) + i])