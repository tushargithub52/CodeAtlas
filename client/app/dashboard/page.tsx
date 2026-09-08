import { AppShell } from '@/components/layout/app-shell'
import { RequireAuth } from '@/components/providers/require-auth'
import React from 'react'

const DashboardPage = () => {
  return (
    <RequireAuth>
        <AppShell hideHeader>
            <div className='flex min-h-svh items-center justify-center'>
                <h1 className='text-3xl font-bold'>Welcome to CodeAtlas</h1>
            </div>
        </AppShell>
    </RequireAuth>
  )
}

export default DashboardPage