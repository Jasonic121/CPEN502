import csv
import matplotlib.pyplot as plt
from matplotlib.ticker import MultipleLocator
import numpy as np

# Initialize a dictionary to store the data in increments of 100
data_by_bin = {}
trials = []
epochsls = []
# Read the data from the CSV file
with open('Graph_EpochFrequency.csv', 'r') as csvfile:
    csvreader = csv.DictReader(csvfile)
    for row in csvreader:
        epoch = int(row['Epochs'])
        epochsls.append(epoch)
        trial = int(row['Trial'])
        trials.append(trial)
        bin_number = int(epoch // 10) * 10 # Calculate the bin number
        if bin_number in data_by_bin:
            data_by_bin[bin_number] += 1
        else:
            data_by_bin[bin_number] = 1

# Sort the data by bin number
sorted_data = sorted(data_by_bin.items())

# Get the last trial number from the list of trials
last_trial = trials[-1]

# Extract the bins and frequencies
bins, frequencies = zip(*sorted_data)

# Calculate the average epochs and standard deviation
average_epochs = np.mean(epochsls)
std_deviation_epochs = np.std(epochsls)
epochs = [epoch for epoch, freq in sorted_data for _ in range(freq)]


# Plot the data as a bar chart
plt.bar(bins, frequencies, width=10, align='center', label='Frequency', edgecolor = 'black')
plt.xlabel('Epoch')
plt.ylabel('Frequency')
plt.title('Epoch Frequency')
plt.legend()
plt.grid(axis='y', linestyle='--', alpha=0.7)

# Customize the x-axis ticks to start from either 2000 or 1000
tick_start = 0  
tick_end = max(bins)  
tick_interval = 10
custom_ticks = range(tick_start, tick_end + tick_interval, tick_interval)
plt.xticks(custom_ticks, rotation=45, )

# Display the average epochs and standard deviation as annotations
plt.annotate(f'Number of Trials: {last_trial}\nAverage Mean of Epochs: {average_epochs:.2f}\nStd Deviation: {std_deviation_epochs:.2f}',
             xy=(0.7, 0.7), xycoords='axes fraction',
             fontsize=10, color='red')


plt.show()
