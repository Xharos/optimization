#!/usr/bin/python
#import matplotlib.pyplot as plt
import numpy as np
import sys

DATA = "data/"
country = sys.argv[1]
x = []
y = []
p = []

x.append(0)
y.append(0)

with open(DATA + country + ".txt", 'r') as reader:
    for line in reader.readlines():
        split = line.split("\t")
        x.append(float(split[1].replace(',', '.')))
        y.append(float(split[0].replace(',', '.')))

p.append(x[0]*y[0])
for i in range(1, len(y)):
    p.append(p[i-1] + y[i]*x[i])

save = 0
for i in range(len(x)):
    save += x[i]
    x[i] = save

poly = np.poly1d(np.polyfit(x, p, deg=2))

#plt.figure()
#plt.subplot(211)
#plt.plot(x, y)
#plt.title("Sum of lin-cost per generator capacities for " + country)
#plt.xlabel("MW")
#plt.ylabel("€/MW")

#t = np.linspace(1, x[-1], len(x)*2)

#plt.subplot(212)
#plt.plot(x, p, 'g', label="Original function")
#plt.plot(t, [np.polyval(poly, t[i]) for i in range (len(t))], 'ro', label="Poly 2 interpolation")
#plt.title("Production cost for " + country)
#plt.xlabel("MW")
#plt.legend()
#plt.ylabel("€")
print(x)
print(p)
#print(poly.c)
print("[" + str(poly.c[0]) + ", " + str(poly.c[1]) + ", " + str(poly.c[2]) + "]")
#plt.title("Country production cost for ", country)
#plt.show()
#print("Compute lagrange 1D interpolation for ", country,", found ", len(x)," data")
