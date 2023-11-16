import os
import matplotlib.pyplot as plt

# File paths
file_paths = [
    '/Users/jason/Courses/UBC/CPEN502/CPEN502/out/production/CPEN502/Assignment2/RLRobot/JBot.data/JBot_a=0.1_policy=false_e=0.0_intermediate=false.log',
    '/Users/jason/Courses/UBC/CPEN502/CPEN502/out/production/CPEN502/Assignment2/RLRobot/JBot.data/JBot_a=0.1_policy=false_e=0.0_intermediate=true.log'
]

# Specify the directory
save_dir = '/Users/jason/Courses/UBC/CPEN502/CPEN502/src/Assignment2/plots'

# Colors for the plots
colors = ['blue', 'red']

# Lists to store data from each file
all_rounds = []
all_win_rates = []

# Read data from each file
for file_path in file_paths:
    rounds = []
    win_rates = []

    with open(file_path, 'r') as file:
        for line in file:
            parts = line.strip().split()
            rounds.append(int(parts[1]))
            win_rates.append(float(parts[3]))

    all_rounds.append(rounds)
    all_win_rates.append(win_rates)

# Plot the data from each file with different colors
for i in range(len(file_paths)):
    plt.plot(all_rounds[i], all_win_rates[i], marker='', color=colors[i], label=f'File {i+1}', linewidth=0.4)

# Add legend
plt.legend(labels=['Only terminal rewards','Have intermediate rewards'])

# Set plot labels and title
plt.title('Win Rate Over Rounds')
plt.xlabel('Round Count')
plt.ylabel('Win Rate (%)')
plt.grid(True)

# Save the plot in the specified directory
plt.savefig(os.path.join(save_dir, 'plot2c_terminal_intermediaterewards.png'))

# Show the plot
plt.show()
