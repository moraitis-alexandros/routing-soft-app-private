import { TruckReadOnlyDto } from "./truck-readonly-dto";
import { LocationNodeReadonlyDto } from "./location-node-readonly-dto";

export interface PlanInsertDto {
      trucksList: TruckReadOnlyDto[];
  locationNodeList: LocationNodeReadonlyDto[];
  algorithmSpec: string;
  timeslotLength: number;
}
