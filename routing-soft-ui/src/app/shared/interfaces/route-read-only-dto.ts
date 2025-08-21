import { LocationNodeReadonlyDto } from "./location-node-readonly-dto";

export interface RouteReadOnlyDto {
    truckId: number;
    truckDescription: string;
    color: string;
    stops: LocationNodeReadonlyDto[];
    legCoordinates?: number[][][];
}