import csv
import matplotlib.pyplot as plt
from matplotlib.ticker import MultipleLocator

# Initialize lists to store the data
epochs = []
average_errors = []

# Read the data from the CSV file
with open('graphing_data.csv', 'r') as csvfile:
    csvreader = csv.DictReader(csvfile)
    for row in csvreader:
        epoch = int(row['Epoch'])
        average_error = float(row['totalError'])
        epochs.append(epoch)
        average_errors.append(average_error)

# Plot the data
plt.plot(epochs, average_errors, label='Total Error')
plt.xlabel('Epoch')
plt.ylabel('Total Error')

# Get the current Axes object
ax = plt.gca()

# Set a larger major locator step for the y-axis to reduce tick density
y_step = 0.1  # Set the desired step size
ax.yaxis.set_major_locator(MultipleLocator(y_step))

# Add a horizontal line at y=0.05
plt.axhline(y=0.05, color='red', linestyle='--', label='y=0.05')

# Find the first epoch where error crosses y=0.05
for epoch, error in zip(epochs, average_errors):
    if error <= 0.05:
        crossing_epoch = epoch
        break

# Annotate the point where it crosses y=0.05
plt.annotate(f'Epoch: {crossing_epoch}', xy=(crossing_epoch, 0.05), xytext=(crossing_epoch, 0.1),
             arrowprops=dict(arrowstyle='->', color='black'))

plt.title('Training Progress')
plt.autoscale(True, axis='x')
plt.legend()
plt.grid(True)
plt.show()
