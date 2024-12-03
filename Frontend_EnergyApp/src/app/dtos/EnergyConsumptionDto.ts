export class EnergyConsumptionDTO {
  hours: string[];
  values: number[];

  constructor(hours: string[], values: number[]) {
    this.hours = hours;
    this.values = values;
  }
}
