export class DeviceDto {
  id: string;
  address: string;
  description: string;
  maxHourlyEnergyConsumption: number;
  user_id: string;

  constructor(id: string, address: string, description: string, maxHourlyEnergyConsumption: number, user_id: string) {
    this.id = id;
    this.address = address;
    this.description = description;
    this.maxHourlyEnergyConsumption = maxHourlyEnergyConsumption;
    this.user_id = user_id;
  }
}
