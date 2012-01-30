/**
 * Copyright (c) 2009, iPlant Collaborative, Texas Advanced Computing Center This software is licensed
 * under the CC-GNU GPL version 2.0 or later. License: http://creativecommons.org/licenses/GPL/2.0/
 */

package org.iplantc.phyloviewer.shared.render;

import org.iplantc.phyloviewer.shared.math.Matrix33;

/**
 * Animates a view matrix linearly from one view matrix to another.
 * While !isDone(), getNextMatrix() returns the next matrix value.
 */
public class AnimateCamera
{
	Matrix33 initialMatrix;
	Matrix33 finalMatrix;
	double u;
	double stepSize;

	/**
	 * Creates a new AnimateCamera
	 * @param initialCamera the starting view matrix
	 * @param finalCamera the ending view matrix
	 * @param numberOfSteps
	 */
	public AnimateCamera(Matrix33 initialCamera, Matrix33 finalCamera, int numberOfSteps)
	{
		this.initialMatrix = initialCamera;
		this.finalMatrix = finalCamera;
		this.u = 0.0;
		this.stepSize = (0 != numberOfSteps ? 1.0 / numberOfSteps : 1);
	}

	/**
	 * @return true if getNextMatrix has been called numberOfSteps or more times
	 */
	public boolean isDone()
	{
		return u >= 1.0;
	}

	/**
	 * @return the next interpolated matrix value. If more than numberOfSteps has already elapsed, then
	 *         the final matrix value is returned.
	 */
	public Matrix33 getNextMatrix()
	{
		if(this.initialMatrix == null || this.finalMatrix == null)
		{
			return new Matrix33();
		}

		u = Math.min(u + stepSize, 1.0);

		Matrix33 result = new Matrix33();
		for(int i = 0;i < 3;++i)
		{
			for(int j = 0;j < 3;++j)
			{
				double difference = finalMatrix.get(i, j) - initialMatrix.get(i, j);
				result.set(i, j, initialMatrix.get(i, j) + (u * difference));
			}
		}

		return result;
	}
}
