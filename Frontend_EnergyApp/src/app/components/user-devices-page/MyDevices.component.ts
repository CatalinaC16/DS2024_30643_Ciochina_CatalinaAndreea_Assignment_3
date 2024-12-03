import {Component, OnInit} from '@angular/core';
import {AuthService} from "../../services/Auth.service";
import {Router} from "@angular/router";
import {UserDto} from "../../dtos/UserDto";
import {UserService} from "../../services/User.service";
import {Role} from "../../dtos/Role";
import {DeviceDto} from "../../dtos/DeviceDto";
import {DeviceService} from "../../services/Device.service";
import {ChartData, ChartOptions, ChartType} from "chart.js";
import {EnergyConsumptionDTO} from "../../dtos/EnergyConsumptionDto";

@Component({
  selector: 'app-my-devices',
  templateUrl: './MyDevices.component.html',
  styleUrls: ['./MyDevices.component.css']
})
export class MyDevicesComponent implements OnInit {
  devices: DeviceDto[] = [];
  errorMessage = '';
  id = '';
  role!: Role;
  selectedDate: Date | null = null;
  deviceCharts: { [deviceId: string]: { type: ChartType; data: ChartData<ChartType>; options: ChartOptions } } = {};

  constructor(
    private deviceService: DeviceService,
    private userService: UserService,
    private router: Router,
    private authService: AuthService
  ) {
  }

  ngOnInit(): void {
    const token = this.authService.getToken();
    let userEmail: string;
    if (token) {
      const payload = JSON.parse(atob(token.split('.')[1]));
      userEmail = payload.sub;
      this.userService.getUserByEmail(userEmail).subscribe(
        (response: UserDto) => {
          this.id = response.id;
          this.role = response.role;
          if (this.id !== '') {
            this.deviceService.getDevicesByUserId(this.id).subscribe(
              (data) => {
                this.devices = data;
              },
              (error) => {
                this.errorMessage = 'Could not load devices. Please try again later.';
                console.error('Error fetching devices:', error);
              }
            );
          }
        },
        (error) => {
          this.errorMessage = 'Failed to load user';
          console.error(error);
        }
      );
    }
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(["/login"]).then();
  }

  viewUserDetails() {
    this.router.navigate(["/myProfile"]).then();
  }

  goToHomePage() {
    this.router.navigate(["/home"]).then();
  }

  viewConsumption(deviceId: string, date: Date | null): void {
    if (!date) {
      alert('Please select a date.');
      return;
    }

    const localDate = new Date(date);
    const formattedDate = `${localDate.getFullYear()}-${(localDate.getMonth() + 1).toString().padStart(2, '0')}-${localDate.getDate().toString().padStart(2, '0')}`;
    console.log(formattedDate)

    this.deviceService.getEnergyConsumption(deviceId, formattedDate).subscribe(
      (data: EnergyConsumptionDTO) => {
        console.log(data)
        this.deviceCharts[deviceId] = this.buildChart(data.hours, data.values);
      },
      (error) => {
        console.error('Failed to fetch energy consumption data', error);
      }
    );
  }

  adjustHoursAndValues(labels: string[], values: number[]): { adjustedLabels: string[], adjustedValues: number[] } {
    const adjustedLabels: string[] = [];
    const adjustedValues: number[] = [];

    labels.forEach((label, index) => {
      let hour = parseInt(label.split(':')[0], 10);
      if (hour >= 24) {
        hour = hour - 24;
      }
      adjustedLabels[hour] = `${hour.toString().padStart(2, '0')}:00`;
      adjustedValues[hour] = values[index];
    });

    for (let i = 0; i < 24; i++) {
      if (!adjustedLabels[i]) {
        adjustedLabels[i] = `${i.toString().padStart(2, '0')}:00`;
        adjustedValues[i] = 0;
      }
    }

    return { adjustedLabels, adjustedValues };
  }


  buildChart(labels: string[], data: number[]): { type: ChartType, data: ChartData<ChartType>, options: ChartOptions } {

    const { adjustedLabels, adjustedValues } = this.adjustHoursAndValues(labels, data);

    console.log('Adjusted Labels:', adjustedLabels);
    console.log('Adjusted Values:', adjustedValues);

    return {
      type: 'line',
      data: {
        labels: adjustedLabels,
        datasets: [{
          label: 'Energy Consumption (kWh)',
          data: adjustedValues,
          borderColor: 'blue',
          backgroundColor: 'rgba(0, 123, 255, 0.5)',
          fill: true,
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        scales: {
          x: { title: { display: true, text: 'Hour' } },
          y: { title: { display: true, text: 'Energy Consumption (kWh)' } }
        }
      }
    };
  }

}
